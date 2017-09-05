package cn.com.xiaoyaoji.data;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.exception.ServiceException;
import cn.com.xiaoyaoji.core.util.JsonUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.data.handler.StringResultHandler;
import cn.com.xiaoyaoji.util.JdbcUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author zhoujingjie
 *         created on 2017/8/8
 */
public class UpdateManager {
    private static Logger logger = Logger.getLogger(UpdateManager.class);
    private static UpdateManager instance;

    static {
        instance = new UpdateManager();
    }

    private UpdateManager() {
    }

    public static UpdateManager getInstance() {
        return instance;
    }

    /**
     * 暂时未做升级失败回滚操作
     *
     * @param version
     * @return
     */
    public int update(String version) {
        Connection connection = null;
        try {
            connection = JdbcUtils.getConnect();
            QueryRunner qr = new MyQueryRunner();
            connection.setAutoCommit(true);
            //判断是否已执行更新操作
            String currentVersion = null;
            try {
                currentVersion = qr.query(connection, "select version from sys limit 1", new StringResultHandler());
                if (currentVersion != null && currentVersion.equals(version)) {
                    return 1;
                }
            } catch (SQLException e) {
                //ignore
            }
            if(currentVersion == null){
                currentVersion ="2.0";
            }

            String upgradeFolder = "META-INF/upgrade/";
            File file = new File(this.getClass().getClassLoader().getResource(upgradeFolder).getFile());
            if (!file.exists()) {
                throw new ServiceException(file.getAbsolutePath() + " 不存在 ");
            }
            if (!file.isDirectory()) {
                throw new ServiceException(file.getAbsolutePath() + " 不是文件夹");
            }
            final String tempVersion = currentVersion;
            File[] updateFolders = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (tempVersion == null)
                        return true;
                    return name.replace(".sql","").compareTo(tempVersion) > 0;
                }
            });
            if (updateFolders == null) {
                throw new ServiceException("没有升级文件");
            }
            if (updateFolders.length > 1) {
                Collections.sort(Arrays.asList(updateFolders), new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
            int result = 0;
            QueryRunner queryRunner = new MyQueryRunner();
            Class currentClass = getClass();
            for (File item : updateFolders) {
                String sql = IOUtils.toString(new FileInputStream(item), Constants.UTF8.displayName());
                result += executeSQL(connection, sql, queryRunner);
                String fileVersion = item.getName().replace(".sql", "").replace(".","_");
                try {
                    Method upgradeMethod = currentClass.getDeclaredMethod("update" + fileVersion, Connection.class, QueryRunner.class);
                    result += (int) upgradeMethod.invoke(instance, connection, queryRunner);
                } catch (NoSuchMethodException e) {
                    //ignore
                    logger.info("NoSuchMethodException update"+fileVersion);
                }
                result += qr.update(connection, "insert into sys values('"+fileVersion.replace("_",".")+"')");
            }
            return result;
        }catch (Exception e) {
            throw new RuntimeException("升级失败;" + e.toString(),e);
        } finally {
            JdbcUtils.close(connection);
        }
    }

    private int executeSQL(Connection connection, String upgradeSQL, QueryRunner qr) throws SQLException {
        int rs = 0;
        for (String item : upgradeSQL.split(";")) {
            if (item.trim().length() == 0) {
                continue;
            }
            rs += qr.update(connection, item);
        }
        return rs;
    }


    private int insertAndReset(Connection connection, QueryRunner qr, StringBuilder sql, List<Object> params) throws SQLException {
        sql = sql.delete(sql.length() - 1, sql.length());
        int rs = qr.update(connection, sql.toString(), params.toArray());
        params.clear();
        sql.delete(0, sql.length());
        return rs;
    }

    private List<Interface> getInterfaces(QueryRunner qr, Connection connection, int start, int limit) throws SQLException {
        return qr.query(connection, "select * from interface limit ?,?", new BeanListHandler<>(Interface.class), start, limit);
    }

    private int update2_0(Connection connection, QueryRunner qr) throws IOException, SQLException {
        int rs = 0;

        //批量更新数量
        int batchNum = 100;

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int size = 0;

        SQLBuildResult sbr = null;
        String docInsertSQL = "insert into doc (id,name,sort,type,content,createTime,lastUpdateTime,projectId,parentId) values";
        String docValueSQL = "(?,?,?,?,?,?,?,?,?),";
        //迁移module数据到doc
        List<Module> modules = qr.query(connection, "select * from " + TableNames.MODULES, new BeanListHandler<>(Module.class));
        if (modules != null && modules.size() > 0) {
            sql.append(docInsertSQL);
            //key projectId
            Map<String, JSONArray> globalRequestArgsMap = new HashMap<>();
            Map<String, JSONArray> globalRequestHeadersMap = new HashMap<>();
            int index = 0;
            for (Module m : modules) {
                sql.append(docValueSQL);
                //id,name,sort,type,content,createTime,lastUpdateTime,projectId,parentId
                params.add(m.getId());
                params.add(m.getName());
                params.add(++index);
                params.add(DocType.SYS_FOLDER.getTypeName());
                params.add(null);
                params.add(m.getCreateTime());
                params.add(m.getLastUpdateTime());
                params.add(m.getProjectId());
                params.add("0");

                String requestHeaders = m.getRequestHeaders();
                if (org.apache.commons.lang3.StringUtils.isNoneEmpty(requestHeaders)) {
                    try {
                        JSONArray temp = globalRequestHeadersMap.get(m.getProjectId());
                        if (temp == null) {
                            globalRequestHeadersMap.put(m.getProjectId(), JSON.parseArray(requestHeaders));
                        } else {
                            temp.addAll(JSON.parseArray(requestHeaders));
                        }
                    } catch (Exception e) {
                    }
                }

                String requestArgs = m.getRequestArgs();
                if (org.apache.commons.lang3.StringUtils.isNoneEmpty(requestArgs)) {
                    try {
                        JSONArray temp = globalRequestArgsMap.get(m.getProjectId());
                        if (temp == null) {
                            globalRequestArgsMap.put(m.getProjectId(), JSON.parseArray(requestArgs));
                        } else {
                            temp.addAll(JSON.parseArray(requestArgs));
                        }
                    } catch (Exception e) {
                    }
                }
                if (size >= batchNum) {
                    insertAndReset(connection, qr, sql, params);
                    sql.append(docInsertSQL);
                    size = 0;
                } else {
                    size++;
                }
            }

            if (params.size() > 0) {
                insertAndReset(connection, qr, sql, params);
            }

            sql = new StringBuilder();
            params = new ArrayList<>();
            size = 0;

            {
                //初始化project_global
                Set<String> projectIds = new HashSet<>();
                projectIds.addAll(globalRequestArgsMap.keySet());
                projectIds.addAll(globalRequestHeadersMap.keySet());
                sql.append("insert into project_global (id,environment,http,status,projectId) values");
                String valueSQL = "(?,?,?,?,?),";

                params = new ArrayList<>();
                for (String projectId : projectIds) {
                    String tempSQL = "select environments from " + TableNames.PROJECT + " where id = ?";
                    Project temp = qr.query(connection, tempSQL, new BeanHandler<>(Project.class), projectId);
                    if (temp == null)
                        continue;
                    Map<String, Object> httpMap = new HashMap<>();
                    httpMap.put("requestHeaders", globalRequestHeadersMap.get(projectId));
                    httpMap.put("requestArgs", globalRequestArgsMap.get(projectId));
                    httpMap.put("responseArgs", new String[]{});
                    httpMap.put("responseHeaders", new String[]{});

                    //id,environment,http,status,projectId
                    sql.append(valueSQL);
                    params.add(StringUtils.id());
                    params.add(temp.getEnvironments());
                    params.add(JsonUtils.toString(httpMap));
                    params.add(null);
                    params.add(projectId);

                    if (size >= batchNum) {
                        insertAndReset(connection, qr, sql, params);
                        sql.append("insert into project_global (id,environment,http,status,projectId) values");
                        size = 0;
                    } else {
                        size++;
                    }
                }
                if (params.size() > 0) {
                    insertAndReset(connection, qr, sql, params);
                }
            }
        }


        sql = new StringBuilder();
        params.clear();
        size = 0;

        //迁移interface_folder数据到doc
        List<Folder> folders = qr.query(connection, "select * from " + TableNames.INTERFACE_FOLDER, new BeanListHandler<>(Folder.class));
        if (folders != null && folders.size() > 0) {
            sql.append(docInsertSQL);
            int index = 0;
            for (Folder f : folders) {
                //id,name,sort,type,content,createTime,lastUpdateTime,projectId,parentId
                sql.append(docValueSQL);
                params.add(f.getId());
                params.add(f.getName());
                params.add(++index);
                params.add(DocType.SYS_FOLDER.getTypeName());
                params.add(null);
                params.add(f.getCreateTime());
                params.add(new Date());
                params.add(f.getProjectId());
                params.add(f.getModuleId());


                if (size >= batchNum) {
                    rs += insertAndReset(connection, qr, sql, params);
                    sql.append(docInsertSQL);
                    size = 0;
                } else {
                    size++;
                }
            }
            if (params.size() > 0) {
                rs += insertAndReset(connection, qr, sql, params);
                sql.append(docInsertSQL);
            }
        }
        sql = new StringBuilder();
        size = 0;
        params.clear();

        //迁移interface数据到doc中
        int start = 0, limit = 5000;
        while (true) {
            List<Interface> interfaces = getInterfaces(qr, connection, start, limit);
            if (interfaces != null && interfaces.size() > 0) {
                sql.append(docInsertSQL);
                for (Interface in : interfaces) {
                    sql.append(docValueSQL);
                    String protocol = in.getProtocol();
                    if (org.apache.commons.lang3.StringUtils.isBlank(in.getRequestHeaders())) {
                        in.setRequestHeaders("[]");
                    }
                    if (org.apache.commons.lang3.StringUtils.isBlank(in.getRequestArgs())) {
                        in.setRequestArgs("[]");
                    }
                    if (org.apache.commons.lang3.StringUtils.isBlank(in.getResponseArgs())) {
                        in.setResponseArgs("[]");
                    }
                    if ("DEPRECATED".equals(in.getStatus())) {
                        in.setStatus("已废弃");
                    } else {
                        in.setStatus("有效");
                    }

                    Doc doc = in.toDoc();
                    doc.setId(in.getId());
                    if (doc.getSort() == null) {
                        doc.setSort(0);
                    }

                    if ("HTTP".equals(protocol)) {
                        doc.setType(DocType.SYS_HTTP.getTypeName());
                    } else if ("WEBSOCKET".equals(protocol)) {
                        doc.setType(DocType.SYS_WEBSOCKET.getTypeName());
                    } else {
                        doc.setType(DocType.SYS_DOC_MD.getTypeName());
                    }

                    //id,name,sort,type,content,createTime,lastUpdateTime,projectId,parentId
                    params.add(doc.getId());
                    params.add(doc.getName());
                    params.add(doc.getSort());
                    params.add(doc.getType());
                    params.add(doc.getContent());
                    params.add(doc.getCreateTime());
                    params.add(doc.getLastUpdateTime());
                    params.add(doc.getProjectId());
                    params.add(doc.getParentId());


                    if (size >= batchNum) {
                        insertAndReset(connection, qr, sql, params);
                        sql.append(docInsertSQL);
                        size = 0;
                    } else {
                        size++;
                    }
                }
                if (params.size() > 0) {
                    insertAndReset(connection, qr, sql, params);
                    sql.append(docInsertSQL);
                    size = 0;
                }
                sql = new StringBuilder();
                params.clear();
                size = 0;
                start += limit;
            } else {
                break;
            }
        }

        rs += qr.update(connection, "update doc set content = replace(content,':\"VALID\"','有效')");
        //设置sys为当前版本
        rs += qr.update(connection, "insert into sys values('2.0')");

        return rs;
    }
}
