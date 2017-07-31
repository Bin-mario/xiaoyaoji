package cn.com.xiaoyaoji.core.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.Plugin;

/**
 * doc edit or doc view
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public abstract class DocEvPlugin extends Plugin<DocEvPlugin> {

    public abstract String getEditPage();

    public abstract String getViewPage();

}
