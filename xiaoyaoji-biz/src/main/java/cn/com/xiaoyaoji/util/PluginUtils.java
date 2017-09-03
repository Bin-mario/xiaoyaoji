package cn.com.xiaoyaoji.util;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.plugin.Dependency;
import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public class PluginUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PluginUtils.class);
    private static Map<String,PluginClassLoader> classLoaderMap = new HashMap<>();
    public static String getPluginDir() {
        return ConfigUtils.getProperty("xyj.plugin.dir");
    }

    public static String getPluginSourceDir() {
        return ConfigUtils.getProperty("xyj.plugin.source.dir", "/WEB-INF/plugins/");
    }

    /**
     * 解压插件
     *
     * @param pluginDir 插件位置
     */
    public static void extractPlugins(String pluginDir, String output) {
        File file = new File(pluginDir);
        if (!file.exists()) {
            logger.error(pluginDir + " not exists");
            return;
        }
        if (!file.canRead()) {
            logger.error("can not read plugin in " + pluginDir);
            return;
        }
        if (!file.canWrite()) {
            logger.error("can not write plugin in " + pluginDir);
            return;
        }
        File[] pluginZips = file.listFiles();
        if (pluginZips == null || pluginZips.length == 0) {
            logger.info("the plugin directory is empty");
            return;
        }
        for (File pluginZip : pluginZips) {
            try {
                if (pluginZip.getName().endsWith(".zip")) {
                    String pluginFolderName = FilenameUtils.getBaseName(pluginZip.getName());

                    String pluginSourceFolder = output + File.separator + pluginFolderName;
                    //判断该目录是否存在该文件夹，如果存在则不解压
                    File temp = new File(pluginSourceFolder);
                    if (temp.exists() && temp.isDirectory() && temp.canRead()) {
                        loadPlugin(temp);
                        continue;
                    }
                    extractPlugin(pluginZip,pluginSourceFolder);
                    loadPlugin(temp);
                } else {
                    logger.info("ignore " + pluginZip.getName());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    public static void extractPlugin(File pluginZip,String output) throws Exception {
        ZipUtils.uncompression(pluginZip, output);
        logger.info("extract unzip " + pluginZip.getName() + " success");
    }

    /**
     * 加载插件
     *
     * @param pluginSourceDirURI 已解压后的插件的位置
     */
    public static void loadPlugins(String pluginSourceDirURI) {
        try {
            Path pluginPath = new File(pluginSourceDirURI).toPath();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginPath)) {
                for (Path path : directoryStream) {
                    loadPlugin(path.toFile());
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void reloadPlugin(String pluginId){
        PluginInfo pluginInfo = PluginManager.getInstance().getPluginInfo(pluginId);
        AssertUtils.notNull(pluginInfo,"无效插件");
        try {
            destroyPlugin(pluginId);
            loadPlugin(pluginInfo.getRuntimeDirectory(),pluginId);
        } catch (Exception e) {
            logger.error("reloadPlugin error. pluginId ="+pluginId,e);
        }
    }
    public static void loadPlugin(File pluginDir) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        loadPlugin(pluginDir,null);
    }
    @SuppressWarnings("unchecked")
    private static void loadPlugin(File pluginDir,String reloadId) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (!pluginDir.isDirectory()) {
            return;
        }
        String dir = pluginDir.getAbsolutePath();

        PluginClassLoader classLoader = new PluginClassLoader();
        classLoader.addURL(new File(dir + "/classes/").toURI().toURL());
        File jars = new File(dir + "/libs/");
        if (!(jars.exists() && jars.isDirectory())) {
            return;
        }
        File[] jarFiles = jars.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        if (jarFiles != null) {

            for (File file : jarFiles) {
                String path = file.toURI().toString()+"!/";
                URL jarURL = new URL("jar", "", -1, path);
                classLoader.addURL(jarURL);
            }
        }

        File file = new File(dir + "/plugin.json");
        if (!file.exists()) {
            logger.info("can not find plugin.json in " + dir);
            return;
        }
        if (!file.canRead()) {
            logger.error("can not read plugin in " + dir);
            return;
        }

        try (InputStream in = new FileInputStream(file)) {
            String content = IOUtils.toString(in, Constants.UTF8.displayName());
            List<PluginInfo> pluginInfos = JSON.parseObject(content, new TypeReference<List<PluginInfo>>() {});
            String currentVersion = ConfigUtils.getProperty("xyj.version");
            for (PluginInfo pluginInfo : pluginInfos) {
                if(reloadId!=null){
                    if(!reloadId.equals(pluginInfo.getId())){
                        continue;
                    }
                }
                if(pluginInfo.getDependency()!=null){
                    Dependency dependency = pluginInfo.getDependency();
                    if(StringUtils.isNotBlank(dependency.getMin()) && dependency.getMin().compareTo(currentVersion)>0){
                        logger.error("the plugin {} Minimum dependent version {}, current version {}",pluginInfo.getId(),dependency.getMin(),currentVersion);
                        continue;
                    }

                    if(StringUtils.isNotBlank(dependency.getMax()) && dependency.getMax().compareTo(currentVersion)<0){
                        logger.error("the plugin {} Maxmum dependent version {}, current version {}",pluginInfo.getId(),dependency.getMin(),currentVersion);
                        continue;
                    }
                }
                Plugin plugin = (Plugin) classLoader.loadClass(pluginInfo.getClazz()).newInstance();
                plugin.setPluginInfo(pluginInfo);
                pluginInfo.setPlugin(plugin);
                pluginInfo.setRuntimeFolder(pluginDir.getName());
                pluginInfo.setRuntimeDirectory(pluginDir);

                plugin.init();
                PluginManager.getInstance().register(pluginInfo);
                classLoaderMap.put(pluginInfo.getId(),classLoader);
            }
        } catch (Exception e) {
            logger.error(file.getAbsolutePath(), e);
        }
    }

    public static void destroyPlugin(String pluginId){
        PluginInfo pluginInfo = PluginManager.getInstance().getPluginInfo(pluginId);
        AssertUtils.notNull(pluginInfo,"无效插件");
        PluginManager.getInstance().unload(pluginInfo);
        classLoaderMap.get(pluginId).unloadJar();
    }

    public static void deletePlugin(String pluginId){
        PluginManager pluginManager = PluginManager.getInstance();
        PluginInfo pluginInfo = pluginManager.getPluginInfo(pluginId);
        AssertUtils.notNull(pluginInfo,"无效插件");
        try {
            String packageJSONFileName = pluginInfo.getRuntimeDirectory().getCanonicalPath()+File.separator+"plugin.json";
            FileInputStream fis = new FileInputStream(packageJSONFileName);
            String content = IOUtils.toString(fis,"UTF-8");
            IOUtils.closeQuietly(fis);
            List<PluginInfo> pluginInfos = JSON.parseArray(content,PluginInfo.class);
            for(PluginInfo temp:pluginInfos){
                //卸载插件
                try {
                    destroyPlugin(temp.getId());
                    if(pluginInfo.getRuntimeDirectory().exists()) {
                        //删除文件夹
                        FileUtils.deleteDirectory(pluginInfo.getRuntimeDirectory());
                    }
                    //删除zip文件
                    String zipFileName = pluginInfo.getRuntimeDirectory().getParent()+File.separator+pluginInfo.getRuntimeFolder()+".zip";
                    FileUtils.deleteQuietly(new File(zipFileName));
                } catch (Exception e) {
                    logger.error("delete plugin error pluginId="+pluginId,e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }

    private static ClassLoader getParentClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = PluginUtils.class.getClassLoader();
        }
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    static class PluginClassLoader extends URLClassLoader {
        private List<JarURLConnection> cachedJarFiles = new ArrayList<JarURLConnection>();
        public PluginClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public PluginClassLoader() {
            super(new URL[]{}, getParentClassLoader());

        }

        public void addURL(URL file) {
            super.addURL(file);
            try {
                URLConnection uc = file.openConnection();
                if (uc instanceof JarURLConnection) {
                    uc.setUseCaches(true);
                    ((JarURLConnection) uc).getManifest();
                    cachedJarFiles.add((JarURLConnection)uc);
                }
            } catch (IOException e) {
                logger.error("failed to cache plugin jar file="+file.toExternalForm(),e);
            }
        }

        public void unloadJar(){
            for(JarURLConnection connection:cachedJarFiles){
                try {
                    connection.getJarFile().close();
                } catch (IOException e) {
                    logger.error("close jar file connection failure",e);
                }
            }
        }
    }
}
