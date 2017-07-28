package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class MarkdownDocPlugin extends DocEvPlugin{


    @Override
    public String getEditPage() {
        return "markdown/edit.jsp";
    }

    @Override
    public String getViewPage() {
        return "markdown/view.jsp";
    }

}
