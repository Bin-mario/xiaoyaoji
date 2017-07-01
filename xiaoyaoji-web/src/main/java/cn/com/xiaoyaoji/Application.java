package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

/**
 * @author zhoujingjie
 * @date 2016-07-26
 */
public class Application {
    private static Logger logger = Logger.getLogger(Application.class);

    public static void started(ServletContext servletContext) {
        //MessageBus.instance().register(new MessageNotify());
        initBeanUtilsConfig();
        //todo 清理回收站超过30天的项目
        //todo 功能模块化

        extractPlugins(Config.PLUGINS_DIR);
        try {
            loadPlugins(servletContext.getResource(Config.PLUGINS_SOURCE_DIR).toURI());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * beanutils 日期格式化
     */
    private static void initBeanUtilsConfig(){
        DateConverter converter = new DateConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(converter, Date.class);
    }

    /**
     * 解压插件
     * @param pluginDir 插件位置
     */
    private static void extractPlugins(String pluginDir){
        File file = new File(pluginDir);
        if(!file.exists()){
            logger.error(pluginDir+" not exists");
            return;
        }
        if(!file.canRead()){
            logger.error("can not read plugin in "+pluginDir);
            return;
        }
        if(!file.canWrite()){
            logger.error("can not write plugin in "+pluginDir);
            return;
        }
        File[] pluginZips = file.listFiles();
        if(pluginZips==null || pluginZips.length == 0){
            logger.info("the plugin directory is empty");
            return;
        }
        for(File pluginZip:pluginZips){

        }
    }
    /**
     * 加载插件
     * @param pluginSourceDirURI 已解压后的插件的位置
     */
    private static void loadPlugins(URI pluginSourceDirURI){
        try {
            Path pluginPath = Paths.get(pluginSourceDirURI);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginPath)) {
                for (Path path : directoryStream) {
                    loadPlugin(path.toFile());
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(),ex);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }

    private static void loadPlugin(File pluginDir) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String dir = pluginDir.getAbsolutePath();
        URL[] urls = new URL[]{
                new File(dir+"/classes/").toURI().toURL(),
                new File(dir+"/libs/").toURI().toURL(),
                new File(dir+"/libs/").toURI().toURL()
        };
        URLClassLoader classLoader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
        File file = new File(dir+"/plugin.json");
        if(!file.exists()){
            logger.info("can not find plugin.json in "+dir);
            return;
        }
        if(file.canRead()){
            logger.error("can not read plugin in "+dir);
            return;
        }

        try(InputStream in = new FileInputStream(file)) {
            String content = IOUtils.toString(in, Constants.UTF8.displayName());
            List<PluginInfo> pluginInfos = JSON.parseObject(content, new TypeReference<List<PluginInfo>>(){});
            for(PluginInfo pluginInfo:pluginInfos) {
                Plugin plugin = (Plugin) classLoader.loadClass(pluginInfo.getClazz()).newInstance();
                pluginInfo.setPlugin(plugin);
                PluginManager.getInstance().register(pluginInfo);
            }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

}
