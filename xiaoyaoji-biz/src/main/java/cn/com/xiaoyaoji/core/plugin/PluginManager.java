package cn.com.xiaoyaoji.core.plugin;

import cn.com.xiaoyaoji.core.plugin.doc.DocEvPlugin;
import cn.com.xiaoyaoji.core.plugin.doc.DocExportPlugin;
import cn.com.xiaoyaoji.core.plugin.doc.DocImportPlugin;

import java.util.*;
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

    public PluginInfo<DocExportPlugin> getExportPlugin(String pluginId){
        List<PluginInfo> list = getPlugins(Event.DOC_EXPORT);
        for(PluginInfo temp:list){
            if(temp.getId().equals(pluginId)){
                return temp;
            }
        }
        return null;
    }
    public PluginInfo<DocImportPlugin> getImportPlugin(String pluginId){
        List<PluginInfo> list = getPlugins(Event.DOC_IMPORT);
        for(PluginInfo temp:list){
            if(temp.getId().equals(pluginId)){
                return temp;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<PluginInfo<DocEvPlugin>> getDocEvPlugins(){
        List<PluginInfo> pluginInfos = getPlugins(Event.DOC_EV);
        List<PluginInfo<DocEvPlugin>> temp = new ArrayList<>(pluginInfos.size());
        for(PluginInfo item:pluginInfos){
            temp.add(item);
        }
        return temp;
    }


}
