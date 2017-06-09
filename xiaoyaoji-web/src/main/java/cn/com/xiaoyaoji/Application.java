package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.core.plugin.ImportPlugin;
import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginType;
import cn.com.xiaoyaoji.extension.asynctask.message.MessageBus;
import cn.com.xiaoyaoji.extension.asynctask.message.MessageNotify;
import cn.com.xiaoyaoji.extension.impexp.ImportManager;
import cn.com.xiaoyaoji.utils.ClassUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Date;

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

        if(true)
            return;
        try {
            URI uri = Thread.currentThread().getContextClassLoader().getResource("/").toURI();
            Path pluginPath = Paths.get(uri);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginPath)) {
                for (Path path : directoryStream) {
                    Path filePath =path.resolve("plugin.json");
                    readPlugin(filePath.toFile());
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(),ex);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }

    private static void readPlugin(File pluginJsonFile){
        try(InputStream in = new FileInputStream(pluginJsonFile)) {
            String content = IOUtils.toString(in, Charsets.UTF_8);
            Plugin plugin = JSON.parseObject(content, Plugin.class);
            if(plugin.getType().equals(PluginType.DOC.name())){
                ImportPlugin im = ClassUtils.newInstance(plugin.getClazz(),ImportPlugin.class);
                ImportManager.instance().register(im);
            }
            //todo 其他插件
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

}
