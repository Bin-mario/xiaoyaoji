package cn.com.xiaoyaoji.core.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.AbstractPlugin;

/**
 * doc edit or doc view
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public abstract class DocEvPlugin extends AbstractPlugin<DocEvPlugin> {

    public abstract String getEditPage();

    public abstract String getViewPage();

}
