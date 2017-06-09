package cn.com.xiaoyaoji.core.plugin;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public interface ImportPlugin {

    boolean support(String sourceFileType,String targetFileType);

    Object doImport(Object data);
}
