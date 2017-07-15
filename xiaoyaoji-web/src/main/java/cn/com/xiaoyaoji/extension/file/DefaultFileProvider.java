package cn.com.xiaoyaoji.extension.file;

import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.integration.file.AbstractFileProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 默认文件操作
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class DefaultFileProvider extends AbstractFileProvider {
    /**
     * 上传
     *
     * @param path  文件路径
     * @param bytes 文件流
     * @return 上传后的文件夹名称
     */
    @Override
    public String upload(String path, byte[] bytes) throws IOException {
        Path targetPath = Paths.get(ConfigUtils.getProperty("file.upload.dir"), path);
        Path parent = targetPath.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        InputStream in = new ByteArrayInputStream(bytes);
        Files.copy(in,targetPath, StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    @Override
    public boolean delete(String path) throws IOException {
        Path p = Paths.get(ConfigUtils.getProperty("file.upload.dir") + path);
        Files.delete(p);
        return !Files.exists(p);
    }
}
