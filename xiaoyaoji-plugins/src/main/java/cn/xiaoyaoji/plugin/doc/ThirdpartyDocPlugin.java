package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/22
 */
public class ThirdpartyDocPlugin extends DocEvPlugin {
    @Override
    public String getEditPage() {
        return "thirdparty/edit.jsp";
    }

    @Override
    public String getViewPage() {
        return "thirdparty/view.jsp";
    }

}
