package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.ProjectGlobal;
import cn.com.xiaoyaoji.utils.ResultUtils;

import java.util.*;

/**
 * @author: zhoujingjie
 * @Date: 17/4/16
 */
public class ProjectService {
    private static ProjectService service;
    static {
        service = new ProjectService();
    }
    private ProjectService(){}
    public static ProjectService instance(){
        return service;
    }

    /**
     * 获取属性菜单的文档
     * @param projectId 项目id
     * @return docs
     */
    public List<Doc> getProjectDocs(String projectId){
        // 获取该项目下所有接口
        List<Doc> docs = ResultUtils.list(ServiceFactory.instance().getDocsByProjectId(projectId));
        return treeDocs(docs);
    }

    public List<Doc> getProjectDocs(String projectId, boolean full){
        // 获取该项目下所有接口
        List<Doc> docs = ResultUtils.list(ServiceFactory.instance().getDocsByProjectId(projectId, full));
        return treeDocs(docs);
    }

    private List<Doc> treeDocs(List<Doc> docs ){
        Map<String,List<Doc>> docMap = new LinkedHashMap<>();
        //root
        docMap.put("0",new ArrayList<Doc>());
        //
        for(Doc doc:docs){
            docMap.put(doc.getId(),doc.getChildren());
        }
        for(Doc doc:docs){
            List<Doc> temp = docMap.get(doc.getParentId());
            if(temp!=null){
                temp.add(doc);
            }
        }
        return docMap.get("0");
    }

    public ProjectGlobal getProjectGlobal(String projectId){
        return DataFactory.instance().getProjectGlobal(projectId);
    }

    /**
     * 修改最后更新时间
     * @param projectId
     * @return
     */
    public boolean updateLastUpdateTime(String projectId){
        Project project = new Project();
        project.setId(projectId);
        project.setLastUpdateTime(new Date());
        return DataFactory.instance().update(project)>0;
    }

    public boolean checkProjectIsPublic(String projectId) {
        return DataFactory.instance().checkProjectIsPublic(projectId);
    }
}

