package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class HttpDocPlugin implements DocEvPlugin{


    @Override
    public String getEditPage() {
        return "http/edit.jsp";
    }

    @Override
    public String getViewPage() {
        return "http/view.jsp";
    }

    @Override
    public String getContextPath() {
        return "cn.xiaoyaoji.doc";
    }
}
