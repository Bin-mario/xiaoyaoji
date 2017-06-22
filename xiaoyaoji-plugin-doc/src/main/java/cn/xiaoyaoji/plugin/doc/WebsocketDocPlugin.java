package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.DocEvPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class WebsocketDocPlugin implements DocEvPlugin{


    @Override
    public String getEditPage() {
        return "websocket/edit.jsp";
    }

    @Override
    public String getViewPage() {
        return "websocket/view.jsp";
    }

    @Override
    public String getContextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
