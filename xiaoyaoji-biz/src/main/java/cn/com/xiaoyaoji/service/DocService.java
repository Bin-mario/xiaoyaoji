package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.common.Message;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ResultUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.DocHistory;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author: zhoujingjie
 * @Date: 17/4/16
 */
public class DocService {
    private static Logger logger = Logger.getLogger(DocService.class);
    private static DocService service;
    private DocService(){
    }
    static {
        service= new DocService();
    }
    public static DocService instance(){
        return service;
    }


    public Doc getDoc(String id ){
        return DataFactory.instance().getById(Doc.class,id);
    }

    public List<Doc> getDocs(String parentId) {
        return ResultUtils.list(DataFactory.instance().getDocs(parentId));
    }

    public Doc getByHistoryId(String historyId) {
        DocHistory history = DataFactory.instance().getById(DocHistory.class,historyId);
        if(history != null){
            Doc doc = new Doc();
            try {
                BeanUtils.copyProperties(doc,history);
                doc.setId(history.getDocId());
                doc.setLastUpdateTime(history.getCreateTime());
                if(doc.getContent() == null){
                    doc.setContent("{}");
                }
                return doc;
            } catch (IllegalAccessException |InvocationTargetException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }


    // 创建默认分类
    public Doc createDefaultDoc(String projectId) {
        Doc doc = new Doc();
        doc.setId(StringUtils.id());
        doc.setName("默认文档");
        doc.setCreateTime(new Date());
        doc.setSort(0);
        doc.setProjectId(projectId);
        doc.setLastUpdateTime(new Date());
        doc.setParentId("0");
        doc.setType(DocType.SYS_DOC_RICH_TEXT.getTypeName());
        int rs = ServiceFactory.instance().create(doc);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return doc;
    }

    public List<Doc> searchDocs(String text, String projectId) {
        return ResultUtils.list(DataFactory.instance().searchDocs(text,projectId));
    }

    public String getFirstDocId(String projectId) {
        return DataFactory.instance().getFirstDocId(projectId);
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
}
