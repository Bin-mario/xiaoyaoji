package cn.com.xiaoyaoji.extension.email;

import cn.com.xiaoyaoji.core.util.ConfigUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * @author: zhoujingjie
 * @Date: 16/8/21
 */
public class EMailUtils {
    private static Logger logger = Logger.getLogger(EMailUtils.class);

    private static EmailProvider provider;
    static {
        String emailProvider = ConfigUtils.getProperty("email.provider");
        try {
            provider = (EmailProvider) Class.forName(emailProvider).newInstance();
        } catch (InstantiationException |IllegalAccessException |ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
    }
    public static void sendCaptcha(String code, String to) {
        provider.sendCaptcha(code,to);
    }

    public static void findPassword(String id, String to) {
        String findpasswordURL = ConfigUtils.getProperty("xyj.findpassword.url");
        String findPageURL = findpasswordURL+"?token=" + Base64.encodeBase64String((id + "!" + to).getBytes());
        provider.findPassword(findPageURL,to);
    }



}
