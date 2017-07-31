package cn.com.xiaoyaoji.core.plugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public abstract class Plugin<T extends Plugin> {

    private PluginInfo<T> pluginInfo;

    public PluginInfo<T> getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(PluginInfo<T> pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public void init(){}

    public void destory(){}
}
