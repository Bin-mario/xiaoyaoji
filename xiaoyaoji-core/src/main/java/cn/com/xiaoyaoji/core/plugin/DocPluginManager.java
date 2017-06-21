package cn.com.xiaoyaoji.core.plugin;

import java.util.List;

/**
 * @author zhoujingjie
 *         created on 2017/6/21
 */
public class DocPluginManager {

    private DocPluginManager(){}
    private static DocPluginManager instance;
    static {
        instance = new DocPluginManager();
    }
    public static DocPluginManager getInstance(){
        return instance;
    }

    public DocPlugin getPlugin(String docType){
        List<Plugin> plugins = PluginManager.getInstance().getPlugins(PluginType.DOC);
        for(Plugin plugin:plugins){
            if(!(plugin instanceof DocPlugin)){
                continue;
            }
            DocPlugin dp = (DocPlugin) plugin;
            if(dp.support(docType)){
                return dp;
            }
        }
        return null;
    }
}
