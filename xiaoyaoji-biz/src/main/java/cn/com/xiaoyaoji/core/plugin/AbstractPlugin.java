package cn.com.xiaoyaoji.core.plugin;

/**
 * 抽象插件类
 * @author zhoujingjie
 *         created on 2017/7/28
 */
public abstract class AbstractPlugin<T extends Plugin> implements Plugin{
    private PluginInfo<T> pluginInfo;

    public PluginInfo<T> getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(PluginInfo<T> pluginInfo) {
        this.pluginInfo = pluginInfo;
    }


}
