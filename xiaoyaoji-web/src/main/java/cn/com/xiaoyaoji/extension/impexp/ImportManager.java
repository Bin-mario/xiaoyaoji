package cn.com.xiaoyaoji.extension.impexp;

import cn.com.xiaoyaoji.core.plugin.ExportPlugin;
import cn.com.xiaoyaoji.core.plugin.ImportPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class ImportManager {

    private static ImportManager instance;
    static {
        instance = new ImportManager();
    }
    private Set<ImportPlugin> importPlugins =new HashSet<>();
    private ImportManager(){}

    public static ImportManager instance(){
        return instance;
    }

    public void register(ImportPlugin importPlugin){
        importPlugins.add(importPlugin);
    }

    public Object doImport(String sourceFileType,String targetFileType,Object data){
        for(ImportPlugin plugin:importPlugins){
            if(plugin.support(sourceFileType,targetFileType)){
                return plugin.doImport(data);
            }
        }
        return null;
    }
}
