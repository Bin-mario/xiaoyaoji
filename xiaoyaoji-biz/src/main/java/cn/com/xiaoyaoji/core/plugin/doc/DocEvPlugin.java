package cn.com.xiaoyaoji.core.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.Plugin;

/**
 * doc edit or doc view
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public interface DocEvPlugin extends Plugin {

    String getEditPage();

    String getViewPage();

    String getContextPath();
}
