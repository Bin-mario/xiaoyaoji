package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.core.plugin.AbstractDocPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class WebsocketDocPlugin extends AbstractDocPlugin{

    @Override
    public boolean support(String docType) {
        return DocType.SYS_WEBSOCKET.getTypeName().equals(docType);
    }

    @Override
    public String getEditModePage() {
        return "websocket/edit.jsp";
    }

    @Override
    public String getViewModePage() {
        return "websocket/view.jsp";
    }

    @Override
    public String contextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
