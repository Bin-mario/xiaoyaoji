package cn.com.xiaoyaoji.integration.file;

import cn.com.xiaoyaoji.core.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public abstract class AbstractFileProvider implements FileProvider{
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
    @Override
    public String uploadByURL(String url) throws IOException {
        byte[] bytes = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        return upload(path(""),bytes);
    }
}
