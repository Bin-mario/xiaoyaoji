package cn.com.xiaoyaoji.extension.file;

import cn.com.xiaoyaoji.core.common.FileType;
import cn.com.xiaoyaoji.utils.ConfigUtils;
import cn.com.xiaoyaoji.utils.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件上传
 *
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);
    private static FileProvider fileUploadProvider;
    static {
        String uploadProviderClasss = ConfigUtils.getProperty("file.upload.provider");
        try {
            fileUploadProvider = (FileProvider) Class.forName(uploadProviderClasss).newInstance();
        } catch (InstantiationException |IllegalAccessException | ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static MetaData upload(MultipartFile file) throws IOException {
        MetaData md = new MetaData();
        if(file.getContentType()!=null && file.getContentType().startsWith("image")){
            md.setType(FileType.IMG);
        }else{
            md.setType(FileType.FILE);
        }
        String path = path(getExtension(file.getOriginalFilename()));
        md.setPath(fileUploadProvider.upload(path,IOUtils.toByteArray(file.getInputStream())));
        return md;
    }

    private static String getExtension(String filename){
        String ext = FilenameUtils.getExtension(filename);
        if(ext == null){
            ext = "";
        }else{
            ext ="."+ext;
        }
        return ext;
    }
    public static String path(String ext) {
        if(ext == null)
            ext="";
        return new SimpleDateFormat("yyyyMM/dd/").format(new Date()).concat(StringUtils.uuid() + ext);
    }




    /**
     * 根据地址上传
     *
     * @param url http url
     * @return
     * @throws IOException
     */
    public static String uploadByURL(String url) throws IOException {
        InputStream in=null;
        try {
            in = new URL(url).openConnection().getInputStream();
            byte[] bytes = IOUtils.toByteArray(in);
            return fileUploadProvider.upload(path(""),bytes);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static void delete(String path) throws IOException {
        if (path == null)
            return;
        fileUploadProvider.delete(path);
    }
}
