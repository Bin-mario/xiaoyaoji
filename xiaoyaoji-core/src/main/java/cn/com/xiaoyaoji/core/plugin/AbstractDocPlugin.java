package cn.com.xiaoyaoji.core.plugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public abstract class AbstractDocPlugin extends AbstractPlugin implements DocPlugin{
    @Override
    public PluginType getPluginType() {
        return PluginType.DOC;
    }


}
