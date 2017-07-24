package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.util.PluginUtils;
import cn.com.xiaoyaoji.utils.ZipUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.*;
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

        try {
            String pluginsDir = PluginUtils.getPluginDir();
            String outputURI =servletContext.getResource(PluginUtils.getPluginSourceDir()).toURI().getPath();
            extractPlugins(pluginsDir,outputURI);

            loadPlugins(outputURI);
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
    private static void extractPlugins(String pluginDir,String output){
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
            try {
                if(pluginZip.getName().endsWith(".zip")) {
                    ZipUtils.uncompression(pluginZip, output + "/" + FilenameUtils.getBaseName(pluginZip.getName()));
                    logger.info("extract unzip " + pluginZip.getName()+" success");
                }else{
                    logger.info("ignore "+pluginZip.getName());
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
    /**
     * 加载插件
     * @param pluginSourceDirURI 已解压后的插件的位置
     */
    private static void loadPlugins(String pluginSourceDirURI){
        try {
            Path pluginPath = new File(pluginSourceDirURI).toPath();
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

    @SuppressWarnings("unchecked")
    private static void loadPlugin(File pluginDir) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(!pluginDir.isDirectory()){
            return;
        }
        String dir = pluginDir.getAbsolutePath();

        PluginClassLoader classLoader = new PluginClassLoader();
        classLoader.addURL(new File(dir+"/classes/").toURI().toURL());
        File jars = new File(dir+"/libs/");
        if(!(jars.exists() && jars.isDirectory())){
            return;
        }
        File[] jarFiles =jars.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        if(jarFiles !=null){

            for(File file : jarFiles){
                classLoader.addURL(file.toURI().toURL());
            }
        }

        File file = new File(dir+ "/plugin.json");
        if(!file.exists()){
            logger.info("can not find plugin.json in "+dir);
            return;
        }
        if(!file.canRead()){
            logger.error("can not read plugin in "+dir);
            return;
        }

        try(InputStream in = new FileInputStream(file)) {
            String content = IOUtils.toString(in, Constants.UTF8.displayName());
            List<PluginInfo> pluginInfos = JSON.parseObject(content, new TypeReference<List<PluginInfo>>(){});
            for(PluginInfo pluginInfo:pluginInfos) {
                Plugin plugin = (Plugin) classLoader.loadClass(pluginInfo.getClazz()).newInstance();
                pluginInfo.setPlugin(plugin);
                pluginInfo.setRuntimeFolder(pluginDir.getName());
                PluginManager.getInstance().register(pluginInfo);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    private static ClassLoader getParentClassLoader(){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl == null){
            cl = Application.class.getClassLoader();
        }
        if(cl == null){
            cl= ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    static class PluginClassLoader extends URLClassLoader{

        public PluginClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        public PluginClassLoader(){
            super(new URL[]{},getParentClassLoader());

        }

        public void addURL(URL url) {
            super.addURL(url);
        }
    }
}
