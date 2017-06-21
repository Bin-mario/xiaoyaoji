package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginClassLoader;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

/**
 * @author zhoujingjie
 * @date 2016-07-26
 */
public class Application {
    private static Logger logger = Logger.getLogger(Application.class);
    public static void started(){
        //MessageBus.instance().register(new MessageNotify());
        initFastJsonConfig();
        //todo 清理回收站超过30天的项目
        //todo 功能模块化
        initPlugins();
    }

    /**
     * beanutils 日期格式化
     */
    private static void initFastJsonConfig(){
        DateConverter converter = new DateConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(converter, Date.class);
    }
    private static void initPlugins(){
        try {
            //解压插件
            //加载插件
            //String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"../.."+Config.PLUGINS_SOURCE_DIR;
            String dir="E:\\privatespaces\\apiManager\\xiaoyaoji-web\\target\\xiaoyaoji-2.0.2\\WEB-INF\\plugins";
            Path pluginPath = Paths.get(dir);
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
        try(InputStream in = new FileInputStream(dir+"/plugin.json")) {
            String content = IOUtils.toString(in, Charsets.UTF_8);
            List<PluginInfo> pluginInfos = JSON.parseObject(content, new TypeReference<List<PluginInfo>>(){});
            for(PluginInfo pluginInfo:pluginInfos) {
                Plugin plugin = (Plugin) classLoader.loadClass(pluginInfo.getClazz()).newInstance();
                PluginManager.getInstance().register(plugin);
            }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

}
