package cn.com.xiaoyaoji.data;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.core.common.Pagination;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.handler.IntegerResultHandler;
import cn.com.xiaoyaoji.core.handler.StringResultHandler;
import cn.com.xiaoyaoji.extension.file.FileUtils;
import cn.com.xiaoyaoji.utils.*;
import cn.com.xiaoyaoji.core.exception.SystemErrorException;
import cn.com.xiaoyaoji.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang3.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
public class DataFactory implements Data {
    private static Logger logger = Logger.getLogger(DataFactory.class);
    private static Data instance;

    static {
        final DataFactory impl = new DataFactory();
        instance = (Data) Proxy.newProxyInstance(impl.getClass().getClassLoader(), impl.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke(impl, args);
                } catch (IllegalAccessException e) {
                    return e;
                } catch (IllegalArgumentException e) {
                    return e;
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        });
    }

    private DataFactory() {
    }

    public static Data instance() {
        return instance;
    }

    private <T> T process(Handler<T> handler) {
        Connection connection = null;
        try {
            connection = JdbcUtils.getConnect();
            QueryRunner qr = new MyQueryRunner();
            T data = handler.handle(connection, qr);
            JdbcUtils.commit(connection);
            return data;
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException){
                throw new RuntimeException(e);
            }
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            logger.error(e.getMessage(), e);
            // DbUtils.rollbackAndCloseQuietly(connection);
            throw new RuntimeException(e.getMessage());
        } finally {
            JdbcUtils.close(connection);
        }
    }

    public Connection getConnection() {
        try {
            return JdbcUtils.getConnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryRunner getQueryRunner() {
        return new MyQueryRunner();
    }


    @Override
    public int insert(final Object instance) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateInsertSQL(instance);
                return qr.update(connection, sb.getSql(), sb.getParams());
            }
        });
    }

    @Override
    public int update(final Object instance) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateUpdateSQL(instance);
                return qr.update(connection, sb.getSql(), sb.getParams());
            }
        });
    }

    @Override
    public int delete(final Object instance) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateDeleteSQL(instance);
                return qr.update(connection, sb.getSql(), sb.getParams());
            }
        });
    }

    @Override
    public int delete(final String tableName, final String id) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.update(connection, "delete from " + tableName + " where id =?", id);
            }
        });
    }

    private String getId(Object instance) {
        try {
            return (String) instance.getClass().getMethod("getId").invoke(instance);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    @Override
    public Map<String, Object> getById(final String tableName, final String id) {
        return process(new Handler<Map<String, Object>>() {
            @Override
            public Map<String, Object> handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "select * from " + tableName + " where id = ?";
                return qr.query(connection, sql, new MapHandler(), id);
            }
        });
    }

    @Override
    public <T> T getById(final Class<T> clazz, final String id) {
        return process(new Handler<T>() {
            @Override
            public T handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "select * from " + SqlUtils.getTableName(clazz) + " where id = ?";
                return qr.query(connection, sql, new BeanHandler<>(clazz), id);
            }
        });
    }


    @Override
    public User login(final String email, final String password) {
        return process(new Handler<User>() {
            @Override
            public User handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "select * from user where email=? and password=?";
                return qr.query(connection, sql, new BeanHandler<>(User.class), email, password);
            }
        });
    }

    @Override
    public int updateAndImage(final Object instance, final String... imgKeys) {
        final Map<String, Object> temp = getById(SqlUtils.getTableName(instance.getClass()), getId(instance));
        if (temp == null)
            return 0;
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateUpdateSQL(instance);
                int rs = qr.update(connection, sb.getSql(), sb.getParams());
                if (imgKeys != null && imgKeys.length != 0) {
                    for (String imgKey : imgKeys) {
                        try {
                            FileUtils.delete((String) temp.get(imgKey));
                        } catch (IOException e) {
                            // throw new RuntimeException(e);
                        }
                    }
                }
                return rs;
            }
        });
    }

    @Override
    public User getUserByThirdId(final String thirdId) {
        return process(new Handler<User>() {
            @Override
            public User handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "select * from " + TableNames.USER + " where id = (select userid from " + TableNames.USER_THIRD + " where id=?)";
                return qr.query(connection, sql, new BeanHandler<>(User.class), thirdId);
            }
        });
    }

    @Override
    public int bindUserWithThirdParty(final Thirdparty thirdparty) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                User user = getById(User.class, thirdparty.getUserId());
                AssertUtils.notNull(user, "无效用户");
                //检查是否绑定
                int rs = qr.query(connection, "select count(id) from " + TableNames.USER_THIRD + " where userId=? and type=? and id =?", new IntegerResultHandler(), thirdparty.getUserId(), thirdparty.getType(), thirdparty.getId());
                if (rs == 1)
                    return rs;
                //删除第三方
                rs = qr.update(connection, "delete from " + TableNames.USER_THIRD + " where  id=?", thirdparty.getId());
                // 创建第三方
                StringBuilder thirdSql = new StringBuilder("insert into ");
                thirdSql.append(TableNames.USER_THIRD);
                thirdSql.append(" (id,userid,type) values(?,?,?)");
                rs += qr.update(connection, thirdSql.toString(), thirdparty.getId(), thirdparty.getUserId(), thirdparty.getType());
                if (rs > 0) {
                    if (org.apache.commons.lang3.StringUtils.isBlank(user.getAvatar())) {

                    }
                }
                return rs;
            }
        });
    }

    @Override
    public List<Doc> getDocs(final String parentId) {
        return process(new Handler<List<Doc>>() {
            @Override
            public List<Doc> handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select * from "+TableNames.DOC+" where parentId=? order by sort asc",new BeanListHandler<>(Doc.class),parentId);
            }
        });
    }

    @Override
    public int deleteInterface( final String parentId) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                qr.update(connection, "delete from " + TableNames.INTERFACE_FOLDER + " where id = ?", parentId);
                StringBuilder sql = new StringBuilder();
                sql.append("delete from " + TableNames.DOC);
                sql.append("where parentId=?");
                return qr.update(connection, sql.toString(), parentId);
            }
        });
    }

    @Override
    public List<Team> getTeams(final String userId) {
        return process(new Handler<List<Team>>() {
            @Override
            public List<Team> handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder sql = new StringBuilder("select t.* from ")
                        .append(TableNames.TEAM)
                        .append(" t left join team_user tu on tu.teamId=t.id ")
                        .append(" where tu.userId=? or t.userId=?")
                        .append(" order by tu.createTime desc,t.createTime desc ");

                return qr.query(connection, sql.toString(), new BeanListHandler<>(Team.class), userId, userId);
            }
        });
    }

    @Override
    public List<Project> getProjects(final Pagination pagination) {
        return process(new Handler<List<Project>>() {
            @Override
            public List<Project> handle(Connection connection, QueryRunner qr) throws SQLException {
                String status = (String) pagination.getParams().get("status");
                if(status == null){
                    status ="VALID";
                }
                StringBuilder sql = new StringBuilder("select DISTINCT p.*,u.nickname userName,pu.editable,pu.commonlyUsed from ").append(TableNames.PROJECT)
                        .append(" p left join user u on u.id = p.userId ")
                        .append(" left join project_user pu on pu.projectId = p.id ")
                        .append("  where ( pu.userId=?) and p.status=?")
                        .append(" order by createTime asc");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(Project.class), pagination.getParams().get("userId"),status);
            }
        });
    }

    @Override
    public List<User> getUsersByProjectId(final String projectId) {
        return process(new Handler<List<User>>() {
            @Override
            public List<User> handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder sql = new StringBuilder("select u.id,u.nickname,u.avatar,u.email,pu.editable from user u left join project_user pu on pu.userId=u.id where pu.projectId=?");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(User.class), projectId);
            }
        });
    }

    @Override
    public List<User> getAllProjectUsersByUserId(final String userId) {
        return process(new Handler<List<User>>() {
            @Override
            public List<User> handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder sql = new StringBuilder("select u.id,u.nickname,avatar,u.email from " + TableNames.USER + " u \n" +
                        "where u.id in (\n" +
                        "\tselect userId from " + TableNames.PROJECT_USER + " where projectId in (\n" +
                        "\t\tselect projectId from " + TableNames.PROJECT_USER + " where userId=?\n" +
                        "\t)\n" +
                        ")\n");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(User.class), userId);
            }
        });
    }

    //真删除
   /* @Override
    public int deleteTeam(final String id) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                //删除接口
                StringBuilder sql = new StringBuilder("delete from "+TableNames.DOC+" where projectId in (\n" +
                        "\tselect id from project where teamId= ?\n" +
                        ")");
                int rs = qr.update(connection,sql.toString(),id);
                //删除接口文件夹
                sql = new StringBuilder("delete from "+TableNames.INTERFACE_FOLDER+" where projectId in (\n" +
                        "\tselect id from project where teamId= ?\n" +
                        ")");
                rs += qr.update(connection,sql.toString(),id);
                //删除项目操作日志
                sql = new StringBuilder("delete from "+TableNames.PROJECT_LOG+" where projectId in (\n" +
                        "\tselect id from project where teamId= ?\n" +
                        ")");
                rs += qr.update(connection,sql.toString(),id);
                //删除项目与用户关联
                sql = new StringBuilder("delete from project_user where projectId in (\n" +
                        "\tselect id from project where teamId= ?\n" +
                        ")");
                rs += qr.update(connection,sql.toString(),id);
                //删除团队与用户关联
                sql = new StringBuilder("delete from team_user where teamId=?");
                rs += qr.update(connection,sql.toString(),id);
                //删除项目
                sql = new StringBuilder("delete from "+TableNames.PROJECT+" where teamId = ?");
                rs += qr.update(connection,sql.toString(),id);
                //删除团队
                sql = new StringBuilder("delete from "+TableNames.TEAM+" where id = ?");
                rs += qr.update(connection,sql.toString(),id);
                return rs;
            }
        });
    }
    */
    //假删除
    @Override
    public int deleteTeam(final String id) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder sql = new StringBuilder("update " + TableNames.TEAM + " set status=? where id =?");
                int rs = qr.update(connection, sql.toString(), Team.Status.INVALID, id);
                sql = new StringBuilder("update " + TableNames.PROJECT + " set status=? where teamId=?");
                rs += qr.update(connection, sql.toString(), Team.Status.INVALID, id);
                return rs;
            }
        });
    }

    /**
     * //删除项目
     *
     * @param id
     * @return
     */
    @Override
    public int deleteProject(final String id) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                //todo 删除all
                //删除项目
                int rs = qr.update(connection,"delete from "+TableNames.PROJECT+" where id =?",id);
                //删除文档
                rs += qr.update(connection,"delete from "+TableNames.DOC+" where projectid =?",id);
                //删除文档历史
                rs += qr.update(connection,"delete from "+TableNames.DOC_HISTORY +" where projectid =?",id);
                //删除项目与用户关联
                rs += qr.update(connection,"delete from "+TableNames.PROJECT_USER+" where projectid =?",id);
                //删除分享
                rs += qr.update(connection,"delete from "+TableNames.SHARE+" where projectid =?",id);
                return rs;
            }
        });
    }

    @Override
    public List<User> searchUsers(final String key, final String... excludeIds) {
        return process(new Handler<List<User>>() {
            @Override
            public List<User> handle(Connection connection, QueryRunner qr) throws SQLException {
                String n = '%' + key + '%';
                StringBuilder _excludeIds_ = new StringBuilder("\'\',");
                if (excludeIds != null && excludeIds.length > 0) {
                    for (String id : excludeIds) {
                        _excludeIds_.append("\'");
                        _excludeIds_.append(id);
                        _excludeIds_.append("\'");
                        _excludeIds_.append(",");
                    }
                }
                _excludeIds_ = _excludeIds_.delete(_excludeIds_.length() - 1, _excludeIds_.length());
                StringBuilder sql = new StringBuilder("select id,email,nickname from user where  id not in(" + _excludeIds_ + ") and nickname like ? order by length(nickname) asc limit 5");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(User.class), n);
            }
        });
    }

    @Override
    public boolean checkEmailExists(final String email) {
        return process(new Handler<Boolean>() {
            @Override
            public Boolean handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select count(id) from " + TableNames.USER + " where email=?", new IntegerResultHandler(), email) > 0;
            }
        });
    }

    @Override
    public boolean checkProjectUserExists(final String projectId, final String userId) {
        return process(new Handler<Boolean>() {
            @Override
            public Boolean handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select count(id) from " + TableNames.PROJECT_USER + " where projectId=? and userId=?", new IntegerResultHandler(), projectId, userId) > 0;
            }
        });
    }

    @Override
    public int deleteProjectUser(final String projectId, final String userId) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "delete from " + TableNames.PROJECT_USER + " where projectId=? and userId=?";
                return qr.update(connection, sql, projectId, userId);
            }
        });
    }


    @Override
    public List<Doc> getDocsByProjectId(final String projectId) {
        return process(new Handler<List<Doc>>() {
            @Override
            public List<Doc> handle(Connection connection, QueryRunner qr) throws SQLException {
                //不查询content
                StringBuilder sql = new StringBuilder("select id,name,sort,type,parentId,projectId from ").append(TableNames.DOC).append(" where projectId=? order by sort asc");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(Doc.class), projectId);
            }
        });
    }

     @Override
    public List<Doc> getDocsByProjectId(final String projectId, final boolean full) {

        return process(new Handler<List<Doc>>() {
            @Override
            public List<Doc> handle(Connection connection, QueryRunner qr) throws SQLException {
                //不查询content
                String sqlstr = "select id,name,sort,type,parentId,projectId" + (full ? ",content" : "") +" from ";
                StringBuilder sql = new StringBuilder(sqlstr).append(TableNames.DOC).append(" where projectId=? order by sort asc");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(Doc.class), projectId);
            }
        });
    }

    @Override
    public List<String> getDocIdsByParentId(final String parentId) {
        return process(new Handler<List<String>>() {
            @Override
            public List<String> handle(Connection connection, QueryRunner qr) throws SQLException {
                List<Doc> docs =  qr.query(connection,"select id from "+TableNames.DOC+" where parentId=?",new BeanListHandler<>(Doc.class),parentId);
                List<String> ids = new ArrayList<String>();
                if(docs != null){
                    for(Doc temp:docs){
                        ids.add(temp.getId());
                    }
                }
                return ids;
            }
        });
    }

    @Override
    public int deleteByIds(final Class<?> clazz, final List<String> ids) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder deleteSQL = new StringBuilder("delete from "+SqlUtils.getTableName(clazz));
                deleteSQL.append(" where id in (");
                for(String id:ids){
                    deleteSQL.append("?,");
                }
                deleteSQL = deleteSQL.delete(deleteSQL.length()-1,deleteSQL.length());
                deleteSQL.append(")");
                return qr.update(connection,deleteSQL.toString(),ids.toArray());
            }
        });
    }


    @Override
    public String getUserIdByEmail(final String email) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "select id from " + TableNames.USER + " where email =? limit 1";
                return qr.query(connection, sql, new StringResultHandler(), email);
            }
        });
    }

    @Override
    public int findPassword(final String id, final String email, final String password) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                FindPassword fp = getById(FindPassword.class, id);
                AssertUtils.notNull(fp, "无效请求");
                AssertUtils.isTrue(fp.getIsUsed() == 0, "该token已使用");
                AssertUtils.isTrue(fp.getEmail().equals(email), "无效token");
                String newPassword = StringUtils.password(password);
                String sql = new StringBuilder("update ").append(TableNames.USER).append(" set password=? where email=?").toString();
                int rs = qr.update(connection, sql, newPassword, email);
                rs += qr.update(connection, new StringBuilder("update ").append(TableNames.FIND_PASSWORD).append(" set isUsed=1 where id =?").toString(), id);
                return rs;
            }
        });
    }

    @Override
    public boolean checkUserHasProjectEditPermission(final String userId, final String projectId) {
        return process(new Handler<Boolean>() {
            @Override
            public Boolean handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = new StringBuilder("select count(id) from ").append(TableNames.PROJECT_USER).append(" where userId=? and projectId=? and editable='YES'").toString();
                return qr.query(connection, sql, new IntegerResultHandler(), userId, projectId) > 0;
            }
        });
    }

    @Override
    public int importFromRap(final Project project, final List<Module> modules, final List<Folder> folders, final List<Interface> interfaces) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateInsertSQL(project);
                int rs = qr.update(connection, sb.getSql(), sb.getParams());
                ProjectUser pu = new ProjectUser();
                pu.setId(StringUtils.id());
                pu.setCreateTime(new Date());
                pu.setStatus(ProjectUser.Status.ACCEPTED);
                pu.setUserId(project.getUserId());
                pu.setProjectId(project.getId());
                sb = SqlUtils.generateInsertSQL(pu);
                rs += qr.update(connection, sb.getSql(), sb.getParams());
                for (Module m : modules) {
                    sb = SqlUtils.generateInsertSQL(m);
                    rs += qr.update(connection, sb.getSql(), sb.getParams());
                    System.out.println("rs:" + rs + " +module");
                }
                for (Folder f : folders) {
                    sb = SqlUtils.generateInsertSQL(f);
                    rs += qr.update(connection, sb.getSql(), sb.getParams());
                    System.out.println("rs:" + rs + " +folder");
                }
                for (Interface in : interfaces) {
                    sb = SqlUtils.generateInsertSQL(in);
                    rs += qr.update(connection, sb.getSql(), sb.getParams());
                    System.out.println("rs:" + rs + " +in");
                }
                return rs;
            }
        });
    }

    @Override
    public void initUserThirdlyBinds(final User user) {
        process(new Handler<Object>() {
            @Override
            public Object handle(Connection connection, QueryRunner qr) throws SQLException {
                user.setBindQQ(qr.query(connection, "select count(id) from " + TableNames.USER_THIRD + " where userId=? and type='QQ'", new IntegerResultHandler(), user.getId()) > 0);
                user.setBindWeibo(qr.query(connection, "select count(id) from " + TableNames.USER_THIRD + " where userId=? and type='WEIBO'", new IntegerResultHandler(), user.getId()) > 0);
                user.setBindGithub(qr.query(connection, "select count(id) from " + TableNames.USER_THIRD + " where userId=? and type='GITHUB'", new IntegerResultHandler(), user.getId()) > 0);
                return null;
            }
        });
    }


    @Override
    public int removeUserThirdPartyRelation(final String userId, final String type) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.update(connection, "delete from " + TableNames.USER_THIRD + " where userId=? and type=?", userId, type);
            }
        });
    }

    @Override
    public int createProject(final Project project) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                SQLBuildResult sb = SqlUtils.generateInsertSQL(project);
                int rs = qr.update(connection, sb.getSql(), sb.getParams());
                ProjectUser pu = new ProjectUser();
                pu.setUserId(project.getUserId());
                pu.setId(StringUtils.id());
                pu.setCreateTime(new Date());
                pu.setProjectId(project.getId());
                pu.setStatus(ProjectUser.Status.ACCEPTED);
                pu.setEditable(project.getEditable());
                sb = SqlUtils.generateInsertSQL(pu);
                rs += qr.update(connection, sb.getSql(), sb.getParams());
                return rs;
            }
        });
    }

    @Override
    public String getProjectName(final String projectId) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select name from " + TableNames.PROJECT + " where id = ?", new StringResultHandler(), projectId);
            }
        });
    }

    @Override
    public String getInterfaceName(final String interfaceId) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select name from " + TableNames.DOC + " where id = ?", new StringResultHandler(), interfaceId);
            }
        });
    }

    @Override
    public String getProjectEditable(final String projectId, final String userId) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select editable from " + SqlUtils.getTableName(ProjectUser.class) + " where projectId=? and userId=? limit 1", new StringResultHandler(), projectId, userId);
            }
        });
    }

    @Override
    public int updateProjectUserEditable(final String projectId, final String userId, final String editable) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "update " + SqlUtils.getTableName(ProjectUser.class) + " set editable=? where projectId = ? and userId = ?";
                return qr.update(connection, sql, editable, projectId, userId);
            }
        });
    }

    @Override
    public int updateCommonlyUsedProject(final String projectId, final String userId, final String isCommonlyUsed) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                String sql = "update " + SqlUtils.getTableName(ProjectUser.class) + " set commonlyUsed=? where projectId = ? and userId = ?";
                return qr.update(connection, sql, isCommonlyUsed, projectId, userId);
            }
        });
    }

    @Override
    public List<Share> getSharesByProjectId(final String projectId) {
        return process(new Handler<List<Share>>() {
            @Override
            public List<Share> handle(Connection connection, QueryRunner qr) throws SQLException {
                StringBuilder sql = new StringBuilder();
                sql.append("select s.*,u.nickname username from share s\n");
                sql.append("left join user u on u.id = s.userid\n");
                sql.append("where s.projectId = ?");
                return qr.query(connection, sql.toString(), new BeanListHandler<>(Share.class), projectId);
            }
        });
    }

    @Override
    public int updateDocSorts(final String[] idsorts) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                int rs = 0;
                for (String is : idsorts) {
                    String[] temp = is.split("_");
                    if (temp.length == 2) {
                        String id = temp[1], sort = temp[0];
                        rs += qr.update(connection, "update " + TableNames.DOC + " set sort=? where id =?", sort, id);
                    }
                }
                return rs;
            }
        });
    }

    public String getUserName(final String userId) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection, "select nickname from " + TableNames.USER + " where id = ?", new StringResultHandler(), userId);
            }
        });
    }

    @Override
    public List<ProjectLog> getProjectLogs(final Pagination pagination) {
        return process(new Handler<List<ProjectLog>>() {
            @Override
            public List<ProjectLog> handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select pl.*,u.nickname,u.avatar from "+TableNames.PROJECT_LOG+" pl left join user u on u.id = pl.userId where pl.projectId=? order by pl.createTime desc limit ?,?",new BeanListHandler<>(ProjectLog.class),pagination.getParams().get("projectId"),pagination.getStart(),pagination.getLimit());
            }
        });
    }

    @Override
    public int importFromMJSON(final Project project, final List<Module> moduleList) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                //初始化项目
                project.setId(StringUtils.id());
                project.setCreateTime(new Date());
                SQLBuildResult sql = SqlUtils.generateInsertSQL(project);
                int rs = qr.update(connection,sql.getSql(),sql.getParams());

                ProjectUser projectUser = new ProjectUser();
                projectUser.setCreateTime(new Date());
                projectUser.setStatus(ProjectUser.Status.ACCEPTED);
                projectUser.setProjectId(project.getId());
                projectUser.setCommonlyUsed(ProjectUser.CommonlyUsed.NO);
                projectUser.setUserId(project.getUserId());
                projectUser.setEditable(ProjectUser.Editable.YES);
                projectUser.setId(StringUtils.id());
                sql = SqlUtils.generateInsertSQL(projectUser);
                rs = qr.update(connection,sql.getSql(),sql.getParams());

                //初始化模块
                for(Module m:moduleList){
                    m.setProjectId(project.getId());
                    m.setCreateTime(new Date());
                    m.setId(StringUtils.id());
                    sql = SqlUtils.generateInsertSQL(m);
                    rs += qr.update(connection,sql.getSql(),sql.getParams());

                    //初始化文件夹
                    int fi = 1;
                    for(Folder f: m.getFolders()){
                        f.setProjectId(project.getId());
                        f.setModuleId(m.getId());
                        f.setSort(fi++);
                        f.setCreateTime(new Date());
                        f.setId(StringUtils.id());
                        sql = SqlUtils.generateInsertSQL(f);
                        rs += qr.update(connection,sql.getSql(),sql.getParams());

                        //初始化接口
                        int i=1;
                        for(Doc doc :f.getChildren()){
                            doc.setSort(i++);
                            doc.setId(StringUtils.id());
                            doc.setProjectId(project.getId());
                            doc.setCreateTime(new Date());
                            doc.setLastUpdateTime(new Date());
                            sql = SqlUtils.generateInsertSQL(doc);
                            rs += qr.update(connection,sql.getSql(),sql.getParams());

                        }
                    }
                }
                return rs;
            }
        });
    }


    private List<Interface> getInterfaces(QueryRunner qr,Connection connection,int start,int limit) throws SQLException {
        return qr.query(connection,"select * from interface limit ?,?",new BeanListHandler<>(Interface.class),start,limit);
    }

    private Object[] getData(Object obj){
        List<Object> data = new ArrayList<>();
        for(Field field : obj.getClass().getDeclaredFields()){
            if(field.getAnnotation(Ignore.class) == null){
                try {
                    field.setAccessible(true);
                    data.add(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toArray();
    }

    @Override
    public int updateSystem(String version) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                synchronized (this) {


                    //判断是否已执行更新操作
                    try {
                        String version = qr.query(connection,"select version from sys limit 1",new StringResultHandler());
                        if(version != null && version.equals("2.0")){
                            return 1;
                        }
                    } catch (SQLException e) {
                        //ignore
                        //e.printStackTrace();
                    }

                    //批量更新数量
                    int batchNum = 100;


                    //创建doc表
                    int rs =qr.update(connection,"drop table if exists  "+TableNames.DOC);
                    rs += qr.update(connection, "create table "+TableNames.DOC+" (\n" +
                            "\tid char(12) PRIMARY KEY,\n" +
                            "\tname varchar(200),\n" +
                            "    sort int default 100,\n" +
                            "    type varchar(100),\n" +
                            "    content longtext,\n" +
                            "    createTime datetime,\n" +
                            "    lastUpdateTime datetime,\n" +
                            "    parentId char(12),\n" +
                            "    projectId char(12),\n" +
                            "    INDEX `projectId` (`projectId` ASC)\n"+
                            ") ENGINE=InnoDB");
                    //创建doc_history 表
                    rs += qr.update(connection,"drop table if exists "+TableNames.DOC_HISTORY);
                    rs += qr.update(connection,"CREATE TABLE `doc_history` (\n" +
                            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                            "  `name` varchar(200) DEFAULT NULL,\n" +
                            "  `sort` int(11) DEFAULT '100',\n" +
                            "  `type` varchar(100) DEFAULT NULL,\n" +
                            "  `content` longtext,\n" +
                            "  `createTime` datetime DEFAULT NULL,\n" +
                            "  `parentId` char(12) DEFAULT NULL,\n" +
                            "  `projectId` char(12) DEFAULT NULL,\n" +
                            "  `comment` text,\n" +
                            "  `userId` char(12) DEFAULT NULL,\n" +
                            "  `docId` char(12) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n");

                    //创建attach
                    rs += qr.update(connection,"drop table if exists  "+TableNames.ATTACH);
                    rs += qr.update(connection,"CREATE TABLE `"+TableNames.ATTACH+"` (\n" +
                            "  `id` char(12) NOT NULL,\n" +
                            "  `url` varchar(1000) DEFAULT NULL,\n" +
                            "  `type` varchar(45) DEFAULT NULL,\n" +
                            "  `sort` int(11) DEFAULT NULL,\n" +
                            "  `relatedId` char(12) DEFAULT NULL,\n" +
                            "  `fileName` varchar(1000) DEFAULT NULL,\n" +
                            "  `createTime` datetime DEFAULT NULL,\n" +
                            "  `projectId` char(12) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  KEY `normal` (`relatedId`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n");

                    //创建sys表
                    rs += qr.update(connection,"drop table if exists sys ");
                    rs += qr.update(connection, "CREATE TABLE `sys` (\n" +
                            "  `version` varchar(10) DEFAULT NULL\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n");


                    rs +=qr.update(connection,"drop table if exists project_global");
                    rs +=qr.update(connection,"\n" +
                            "CREATE TABLE `"+TableNames.PROJECT_GLOBAL+"` (\n" +
                            "  `id` char(12) NOT NULL DEFAULT '',\n" +
                            "  `environment` mediumtext,\n" +
                            "  `http` mediumtext,\n" +
                            "  `projectId` char(12) NOT NULL DEFAULT '',\n" +
                            "  `status` mediumtext\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n");

                    SQLBuildResult sbr = null;
                    String docInsertSQL ="insert into doc (id,name,sort,type,content,createTime,lastUpdateTime,projectId,parentId) values(?,?,?,?,?,?,?,?,?)";
                    //迁移module数据到doc
                    List<Module> modules = qr.query(connection,"select * from "+TableNames.MODULES,new BeanListHandler<>(Module.class));
                    if(modules!=null && modules.size()>0){
                        //key projectId
                        Map<String,JSONArray> globalRequestArgsMap = new HashMap<>();
                        Map<String,JSONArray> globalRequestHeadersMap = new HashMap<>();
                        List<Object[]> params = new ArrayList<>();
                        String batchSQL = docInsertSQL;
                        for(Module m:modules){
                            Doc doc = new Doc();
                            doc.setId(m.getId());
                            doc.setName(m.getName());
                            doc.setSort(0);
                            doc.setType(DocType.SYS_FOLDER.getTypeName());
                            doc.setCreateTime(m.getCreateTime());
                            doc.setLastUpdateTime(m.getLastUpdateTime());
                            doc.setProjectId(m.getProjectId());
                            doc.setParentId("0");
                            params.add(getData(doc));
                            String requestHeaders = m.getRequestHeaders();
                            if(org.apache.commons.lang3.StringUtils.isNoneEmpty(requestHeaders)){
                                try {
                                    JSONArray temp = globalRequestHeadersMap.get(m.getProjectId());
                                    if(temp == null){
                                        globalRequestHeadersMap.put(m.getProjectId(),JSON.parseArray(requestHeaders));
                                    }else{
                                        temp.addAll(JSON.parseArray(requestHeaders));
                                    }
                                } catch (Exception e) {
                                }
                            }

                            String requestArgs = m.getRequestArgs();
                            if(org.apache.commons.lang3.StringUtils.isNoneEmpty(requestArgs)){
                                try {
                                    JSONArray temp = globalRequestArgsMap.get(m.getProjectId());
                                    if(temp == null){
                                        globalRequestArgsMap.put(m.getProjectId(),JSON.parseArray(requestArgs));
                                    }else{
                                        temp.addAll(JSON.parseArray(requestArgs));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            //100条更新一次
                            if(params.size()>=batchNum){
                                qr.batch(connection, batchSQL, toObjectArr(params));
                                params = new ArrayList<>();
                            }

                        }

                        //初始化project_global
                        Set<String> projectIds = new HashSet<>();
                        projectIds.addAll(globalRequestArgsMap.keySet());
                        projectIds.addAll(globalRequestHeadersMap.keySet());
                        batchSQL = "insert into "+TableNames.PROJECT_GLOBAL+" (id,environment,http,status,projectId) values(?,?,?,?,?)";
                        params = new ArrayList<>();
                        for(String projectId:projectIds) {
                            String sql = "select environments from " + TableNames.PROJECT + " where id = ?";
                            Project temp =  qr.query(connection, sql, new BeanHandler<>(Project.class), projectId);
                            if(temp == null)
                                continue;
                            ProjectGlobal projectGlobal = new ProjectGlobal();
                            Map<String, Object> httpMap = new HashMap<>();
                            httpMap.put("requestHeaders", globalRequestHeadersMap.get(projectId));
                            httpMap.put("requestArgs", globalRequestArgsMap.get(projectId));
                            httpMap.put("responseArgs", new String[]{});
                            httpMap.put("responseHeaders", new String[]{});
                            projectGlobal.setHttp(JsonUtils.toString(httpMap));
                            projectGlobal.setEnvironment(temp.getEnvironments());
                            projectGlobal.setId(StringUtils.id());
                            projectGlobal.setProjectId(projectId);
                            params.add(getData(projectGlobal));
                            if(params.size()>=batchNum){
                                qr.batch(connection,batchSQL, toObjectArr(params));
                                params = new ArrayList<>();
                            }
                            //rs += qr.update(connection,sbr.getSql(),sbr.getParams());
                        }
                    }
                    rs += qr.update(connection,"update doc set content = replace(content,':\"VALID\"','有效')");



                    //迁移interface_folder数据到doc
                    List<Folder> folders = qr.query(connection,"select * from "+TableNames.INTERFACE_FOLDER,new BeanListHandler<>(Folder.class));
                    if(folders!=null && folders.size()>0){
                        String batchSQL = docInsertSQL;
                        List<Object[]> params = new ArrayList<>();
                        for(Folder f:folders){
                            Doc doc = new Doc();
                            doc.setId(f.getId());
                            doc.setName(f.getName());
                            doc.setSort(f.getSort());
                            doc.setType(DocType.SYS_FOLDER.getTypeName());
                            doc.setCreateTime(f.getCreateTime());
                            doc.setLastUpdateTime(new Date());
                            doc.setProjectId(f.getProjectId());
                            doc.setParentId(f.getModuleId());
                            params.add(getData(doc));
                            if(params.size()>=batchNum){
                                qr.batch(connection, batchSQL, toObjectArr(params));
                                params = new ArrayList<>();
                            }
                        }

                    }

                    //迁移interface数据到doc中
                    int start = 0,limit=10000;
                    while (true){
                        List<Interface> interfaces= getInterfaces(qr,connection,start,limit);
                        if(interfaces != null && interfaces.size()>0){
                            List<Object[]> params = new ArrayList<>();
                            String batchSQL = docInsertSQL;
                            for(Interface in:interfaces){
                                Doc doc = new Doc();
                                doc.setId(in.getId());
                                doc.setName(in.getName());
                                doc.setSort(in.getSort()==null?0:in.getSort());
                                String protocol = in.getProtocol();
                                if("HTTP".equals(protocol)) {
                                    doc.setType(DocType.SYS_HTTP.getTypeName());
                                }else if("WEBSOCKET".equals(protocol)){
                                    doc.setType(DocType.SYS_WEBSOCKET.getTypeName());
                                }else{
                                    doc.setType(DocType.SYS_DOC_MD.getTypeName());
                                }
                                String requestHeaders = in.getRequestHeaders();
                                if(org.apache.commons.lang3.StringUtils.isBlank(requestHeaders)){
                                    requestHeaders = "[]";
                                }
                                String requestArgs = in.getRequestArgs();
                                if(org.apache.commons.lang3.StringUtils.isBlank(requestArgs)){
                                    requestArgs = "[]";
                                }
                                String responseArgs = in.getResponseArgs();
                                if(org.apache.commons.lang3.StringUtils.isBlank(responseArgs)){
                                    responseArgs = "[]";
                                }
                                String status = in.getStatus();
                                if("DEPRECATED".equals(status)){
                                    status = "已废弃";
                                }else{
                                    status = "有效";
                                }
                                doc.setContent(JsonUtils.toString(new _HashMap<>()
                                        .add("description",in.getDescription())
                                        .add("url",in.getUrl())
                                        .add("requestMethod",in.getRequestMethod())
                                        .add("contentType",in.getContentType())
                                        .add("requestHeaders", JSON.parse(requestHeaders))
                                        .add("requestArgs", JSON.parse(requestArgs))
                                        .add("responseArgs", JSON.parse(responseArgs))
                                        .add("example",in.getExample())
                                        .add("dataType",in.getDataType())
                                        .add("status",status)
                                ));
                                doc.setCreateTime(in.getCreateTime());
                                doc.setLastUpdateTime(in.getLastUpdateTime());
                                doc.setProjectId(in.getProjectId());
                                doc.setParentId(in.getFolderId());
                                //rs += qr.update(connection,sbr.getSql(),sbr.getParams());
                                //logger.debug("insert content "+ doc.getName()+" success");
                                params.add(getData(doc));

                                if(params.size()>=batchNum){
                                    qr.batch(connection,batchSQL, toObjectArr(params));
                                    params = new ArrayList<>();
                                }
                            }

                            start+=limit;
                        }else {
                            break;
                        }
                    }



                    //更新project 字段
                    rs += qr.update(connection,"ALTER TABLE `project` \n" +
                            "ADD COLUMN `lastUpdateTime` DATETIME NULL AFTER `details`;");
                    rs += qr.update(connection,"DROP TABLE IF EXISTS `sys`;");
                    rs += qr.update(connection,"CREATE TABLE `sys` (\n  `version` varchar(10) DEFAULT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                    //设置sys为当前版本
                    rs += qr.update(connection,"insert into sys values('2.0')");

                    return rs;
                }
            }
        });
    }

    private Object[][] toObjectArr(List<Object[]> list){
        Object[][] arrs = new Object[list.size()][];
        for(int i=0;i<list.size();i++){
            arrs[i] = list.get(i);
        }
        return arrs;
    }

    @Override
    public ProjectGlobal getProjectGlobal(final String projectId) {
        return process(new Handler<ProjectGlobal>() {
            @Override
            public ProjectGlobal handle(Connection connection, QueryRunner qr) throws SQLException {
                ProjectGlobal pg = qr.query(connection,"select * from "+TableNames.PROJECT_GLOBAL+" where projectId=?",new BeanHandler<>(ProjectGlobal.class),projectId);
                if(pg == null){
                    //会有并发问题
                    pg = new ProjectGlobal();
                    pg.setId(StringUtils.id());
                    pg.setProjectId(projectId);
                    pg.setEnvironment("[]");
                    pg.setHttp("{}");
                    pg.setStatus("[{\"name\":\"有效\",\"value\":\"ENABLE\",\"t\":1493901719144},{\"name\":\"废弃\",\"value\":\"DEPRECATED\",\"t\":1493901728060}]");
                    SQLBuildResult sbr = SqlUtils.generateInsertSQL(pg);
                    if(qr.update(connection,sbr.getSql(),sbr.getParams())== 0){
                        throw new SystemErrorException("创建project_global失败");
                    }
                }
                return pg;
            }
        });
    }

    @Override
    public List<Attach> getAttachsByRelatedId(final String relatedId) {
        return process(new Handler<List<Attach>>() {
            @Override
            public List<Attach> handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select * from "+TableNames.ATTACH+" where relatedId=? order by sort asc ,createtime asc",new BeanListHandler<>(Attach.class),relatedId);
            }
        });
    }

    @Override
    public List<DocHistory> getDocHistorys(final String docId) {
        return process(new Handler<List<DocHistory>>() {
            @Override
            public List<DocHistory> handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select h.*,u.nickname userName from "+TableNames.DOC_HISTORY+" h\n" +
                        "left join "+TableNames.USER+" u on u.id = h.userId \n" +
                        "where h.docId=?\n" +
                        "order by createTime desc limit 20 ",new BeanListHandler<>(DocHistory.class),docId);
            }
        });
    }

    @Override
    public int deleteDocHistoryByDocId(final String docId) {
        return process(new Handler<Integer>() {
            @Override
            public Integer handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.update(connection,"delete from "+TableNames.DOC_HISTORY+" where docId=?",docId);
            }
        });
    }

    @Override
    public boolean checkProjectIsPublic(final String projectId) {
        return process(new Handler<Boolean>() {
            @Override
            public Boolean handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select count(1) from "+TableNames.PROJECT+" where permission='PUBLIC' and id=?",new IntegerResultHandler(),projectId)>0;
            }
        });
    }

    @Override
    public List<Doc> searchDocs(final String text, final String projectId) {
        return process(new Handler<List<Doc>>() {
            @Override
            public List<Doc> handle(Connection connection, QueryRunner qr) throws SQLException {
                String t ="%"+text+"%";
                return qr.query(connection,"select id,name from "+TableNames.DOC+" where projectId=? and (name like ? or content like ?) order by sort asc ,createTime desc ",new BeanListHandler<>(Doc.class),projectId,t,t);
            }
        });
    }

    @Override
    public String getFirstDocId(final String projectId) {
        return process(new Handler<String>() {
            @Override
            public String handle(Connection connection, QueryRunner qr) throws SQLException {
                return qr.query(connection,"select id from doc where projectId=? order by sort asc ,createTime asc limit 1",new StringResultHandler(),projectId);
            }
        });
    }
}
