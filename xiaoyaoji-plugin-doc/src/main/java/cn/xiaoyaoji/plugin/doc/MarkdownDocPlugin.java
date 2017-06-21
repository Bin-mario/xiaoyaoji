package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.plugin.AbstractDocPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class MarkdownDocPlugin extends AbstractDocPlugin{

    @Override
    public boolean support(String docType) {
        return DocType.SYS_DOC_MD.getTypeName().equals(docType);
    }

    @Override
    public String getEditModePage() {
        return "markdown/edit.jsp";
    }

    @Override
    public String getViewModePage() {
        return "markdown/view.jsp";
    }

    @Override
    public String contextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
