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

    private Map<Event,List<PluginInfo>> pluginInfos;
    public static PluginManager instance;
    static {
        instance = new PluginManager();
    }

    private PluginManager(){
        pluginInfos = new HashMap<>();
    }

    public static PluginManager getInstance(){
        return instance;
    }

    public void register(PluginInfo pluginInfo){
        Event e = Event.parse(pluginInfo.getEvent());
        List<PluginInfo> temp = pluginInfos.get(e);
        if(temp == null){
            temp = new CopyOnWriteArrayList<>();
            pluginInfos.put(e,temp);
        }
        temp.add(pluginInfo);
    }

    public List<PluginInfo> getPlugins(Event event){
        List<PluginInfo> temp = this.pluginInfos.get(event);
        if(temp == null){
            temp = new CopyOnWriteArrayList<>();
            this.pluginInfos.put(event,temp);
        }
        return temp;
    }
}
