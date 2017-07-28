package cn.xiaoyaoji.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class FolderDocPlugin extends DocEvPlugin{


    @Override
    public String getEditPage() {
        return "folder/edit.jsp";
    }

    @Override
    public String getViewPage() {
        return "folder/view.jsp";
    }

}
