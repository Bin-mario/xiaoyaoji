package cn.com.xiaoyaoji.core.plugin;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class PluginInfo<T extends Plugin>{
    private String id;
    private String name;
    private String description;
    private String author;
    private String createTime;
    private String clazz;
    private String version;
    private String event;
    private Icon icon;
    private T plugin;
    private Dependency dependency;
    //运行时文件夹
    private String runtimeFolder;

    private Map<String,String> config;
    //运行时目录
    private File runtimeDirectory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public T getPlugin() {
        return plugin;
    }

    public void setPlugin(T plugin) {
        this.plugin = plugin;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getRuntimeFolder() {
        return runtimeFolder;
    }

    public void setRuntimeFolder(String runtimeFolder) {
        this.runtimeFolder = runtimeFolder;
    }

    public Map<String, String> getConfig() {
        if(config == null){
            config = new HashMap<>();
        }
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public void setRuntimeDirectory(File runtimeDirectory) {
        this.runtimeDirectory = runtimeDirectory;
    }

    public File getRuntimeDirectory() {
        return runtimeDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginInfo<?> that = (PluginInfo<?>) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }
}

