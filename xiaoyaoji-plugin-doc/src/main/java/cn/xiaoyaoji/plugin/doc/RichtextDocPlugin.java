package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.plugin.AbstractDocPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class RichtextDocPlugin extends AbstractDocPlugin{

    @Override
    public boolean support(String docType) {
        return DocType.SYS_DOC_RICH_TEXT.getTypeName().equals(docType);
    }

    @Override
    public String getEditModePage() {
        return "richtext/edit.jsp";
    }

    @Override
    public String getViewModePage() {
        return "richtext/view.jsp";
    }

    @Override
    public String contextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
