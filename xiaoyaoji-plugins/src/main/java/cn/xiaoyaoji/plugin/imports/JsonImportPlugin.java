package cn.xiaoyaoji.plugin.imports;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.exception.ServiceException;
import cn.com.xiaoyaoji.core.plugin.doc.DocImportPlugin;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public class JsonImportPlugin extends DocImportPlugin {
    private static final String EXPORT_KEY_DOCS = "docs";
    private static final String EXPORT_KEY_VER = "version";
    private static final String IMPORT_KEY_DOC_CHILDREN = "children";
    private static final String GLOBAL = "global";
    private static final String DEFAULT_PARENT_ID = "0";
    @Override
    public void doImport(String fileName, InputStream file, String userId, String projectId, String parentId) throws IOException {
        String json = IOUtils.toString(file, Constants.UTF8.displayName());
        JSONObject obj = null;
        try {
            obj = JSON.parseObject(json);
        } catch (Exception e) {
            throw new ServiceException("数据格式有误");
        }
        String version = obj.getString(EXPORT_KEY_VER);
        if(version == null) {
            import1x(obj,userId);
        }else{
            import2x(obj,userId);
        }

    }

    /**
     * 2.x版本导入
     * @param projectId
     * @param parentId
     * @param docArr
     */
    private void importDoc(String projectId, String parentId, JSONArray docArr){
        for(int i=0; i < docArr.size(); i++){
            JSONObject obj = docArr.getJSONObject(i);
            Doc doc = JSON.toJavaObject(obj, Doc.class);
            doc.setProjectId(projectId);
            doc.setParentId(parentId);
            doc.setId(StringUtils.id());
            ServiceFactory.instance().create(doc);
            JSONArray children = obj.getJSONArray(IMPORT_KEY_DOC_CHILDREN);
            if (children != null && !children.isEmpty()){
                importDoc(projectId, doc.getId(), children);
            }
        }
    }


    private void import2x(JSONObject obj,String userId){
        Project project = JSON.toJavaObject(obj, Project.class);
        project.setId(StringUtils.id());
        project.setUserId(userId);
        project.setCreateTime(new Date());
        project.setLastUpdateTime(new Date());
        ProjectService.instance().createProject(project);
        ProjectGlobal global = obj.getObject(GLOBAL,ProjectGlobal.class);
        if(global!=null){
            global.setId(StringUtils.id());
            global.setProjectId(project.getId());
            ServiceFactory.instance().create(global);
        }
        importDoc(project.getId(), DEFAULT_PARENT_ID,
                obj.getJSONArray(EXPORT_KEY_DOCS));
    }

    /**
     * 1.x版本导入
     * @param obj
     * @param userId
     */
    private void import1x(JSONObject obj,String userId){
        Project project = JSON.toJavaObject(obj.getJSONObject("project"), Project.class);
        project.setId(StringUtils.id());
        project.setUserId(userId);
        project.setCreateTime(new Date());
        project.setLastUpdateTime(new Date());
        ProjectService.instance().createProject(project);

        String projectId = project.getId();

        String env = project.getEnvironments();
        if(org.apache.commons.lang3.StringUtils.isNotBlank(env)){
            ProjectService.instance().createProjectGlobal(project.getId(),env);
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(project.getDetails())){
            Doc doc = new Doc();
            doc.setProjectId(projectId);
            doc.setId(StringUtils.id());
            doc.setParentId("0");
            doc.setName("项目描述");
            doc.setSort(0);
            doc.setCreateTime(new Date());
            doc.setLastUpdateTime(new Date());
            doc.setContent(project.getDetails());
            doc.setType(DocType.SYS_DOC_MD.getTypeName());
            ServiceFactory.instance().create(doc);
        }

        JSONArray modules = obj.getJSONArray("modules");
        if(modules!=null && modules.size()>0){
            for(int i=0;i<modules.size();i++){
                Module module = modules.getObject(i, Module.class);
                Doc doc = module.toDoc();
                doc.setId(StringUtils.id());
                doc.setProjectId(projectId);
                doc.setSort(i);
                ServiceFactory.instance().create(doc);

                if(module.getFolders()!=null){
                    int index=0;
                    for(Folder folder : module.getFolders()){
                        Doc temp = folder.toDoc();
                        temp.setId(StringUtils.id());
                        temp.setParentId(doc.getId());
                        temp.setProjectId(projectId);
                        temp.setSort(index++);
                        ServiceFactory.instance().create(temp);

                        if(folder.getChildren()!=null){
                            for(Interface in:folder.getChildren()){
                                Doc temp2 = in.toDoc();
                                temp2.setId(StringUtils.id());
                                temp2.setParentId(temp.getId());
                                temp2.setProjectId(projectId);
                                ServiceFactory.instance().create(temp2);
                            }
                        }
                    }
                }

            }
        }


    }

}
