package cn.xiaoyaoji.plugin.imports;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.exception.ServiceException;
import cn.com.xiaoyaoji.core.plugin.doc.DocImportPlugin;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public class JsonImportPlugin implements DocImportPlugin {

    private static final String EXPORT_KEY_DOCS = "docs";
    private static final String EXPORT_KEY_VER = "version";
    private static final String IMPORT_KEY_DOC_CHILDREN = "children";
    private static final String DEFAULT_PARENT_ID = "0";
    @Override
    public void doImport(String fileName, InputStream file, String userId, String projectId, String parentId) throws IOException {
        String json = IOUtils.toString(file, Constants.UTF8.displayName());
        JSONObject obj = null;
        try {
            obj = JSON.parseObject(json);
        } catch (Exception e) {
            throw new ServiceException("不支持该文件导入");
        }
        String version = obj.getString(EXPORT_KEY_VER);
        if(version == null) {
            throw new ServiceException("暂不支持该文件导入");
        }
        Project project = JSON.toJavaObject(obj, Project.class);
        project.setId(StringUtils.id());
        project.setUserId(userId);
        ProjectService.instance().createProject(project);
        importDoc(project.getId(), DEFAULT_PARENT_ID, obj.getJSONArray(EXPORT_KEY_DOCS));
    }

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
}
