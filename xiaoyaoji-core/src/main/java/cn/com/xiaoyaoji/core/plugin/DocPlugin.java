package cn.com.xiaoyaoji.core.plugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public interface DocPlugin extends Plugin {

    boolean support(String docType);

    String getEditModePage();

    String getViewModePage();

    String contextPath();
}
