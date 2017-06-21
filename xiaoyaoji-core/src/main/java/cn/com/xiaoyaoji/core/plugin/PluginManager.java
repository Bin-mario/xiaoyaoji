package cn.com.xiaoyaoji.core.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class PluginManager {

    private Map<PluginType,List<Plugin>> plugins;
    public static PluginManager instance;
    static {
        instance = new PluginManager();
    }

    private PluginManager(){
        plugins = new HashMap<>();
    }

    public static PluginManager getInstance(){
        return instance;
    }

    public void register(Plugin plugin){
        List<Plugin> temp = plugins.get(plugin.getPluginType());
        if(temp == null){
            temp = new CopyOnWriteArrayList<>();
            plugins.put(plugin.getPluginType(),temp);
        }
        temp.add(plugin);
    }

    public List<Plugin> getPlugins(PluginType pluginType){
        List<Plugin> temp = this.plugins.get(pluginType);
        if(temp == null){
            temp = new CopyOnWriteArrayList<>();
            this.plugins.put(pluginType,temp);
        }
        return temp;
    }

}
