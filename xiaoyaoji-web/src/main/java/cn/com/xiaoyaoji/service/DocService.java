package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.core.common.Message;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.DocHistory;
import cn.com.xiaoyaoji.data.bean.DocType;
import cn.com.xiaoyaoji.utils.AssertUtils;
import cn.com.xiaoyaoji.utils.ResultUtils;
import cn.com.xiaoyaoji.utils.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

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
}
