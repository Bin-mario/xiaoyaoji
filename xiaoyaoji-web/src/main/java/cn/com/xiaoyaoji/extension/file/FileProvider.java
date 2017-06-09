package cn.com.xiaoyaoji.extension.file;


import java.io.IOException;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public interface FileProvider {


    /**
     * 上传
     * @param path      文件路径
     * @param bytes     文件流
     * @return 上传后的文件夹名称
     */
    String upload(String path,byte[] bytes) throws IOException;

    /**
     * 删除文件
     * @param path  文件路径
     */
    boolean delete(String path) throws IOException;
}
