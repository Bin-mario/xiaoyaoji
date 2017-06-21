package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.plugin.AbstractDocPlugin;
import cn.com.xiaoyaoji.core.plugin.AbstractPlugin;
import cn.com.xiaoyaoji.core.plugin.PluginType;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class HttpDocPlugin extends AbstractDocPlugin{

    @Override
    public boolean support(String docType) {
        return DocType.SYS_HTTP.getTypeName().equals(docType);
    }

    @Override
    public String getEditModePage() {
        return "http/edit.jsp";
    }

    @Override
    public String getViewModePage() {
        return "http/view.jsp";
    }

    @Override
    public String contextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
