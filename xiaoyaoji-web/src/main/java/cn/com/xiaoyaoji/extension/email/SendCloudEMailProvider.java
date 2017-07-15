package cn.com.xiaoyaoji.extension.email;

import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.core.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class SendCloudEMailProvider implements EmailProvider {
    private static Logger logger = Logger.getLogger(SendCloudEMailProvider.class);
    private static String TEMPLATE_URL = "http://sendcloud.sohu.com/webapi/mail.send_template.json";

    @Override
    public void sendCaptcha(String code, String to) {
        String vars = JSON.toJSONString(new _HashMap<>().add("to", new String[]{to}).add("sub", new _HashMap<>().add("%captcha%", new String[]{code})));
        NameValuePair[] pairs = new NameValuePair[]{new NameValuePair("api_user", ConfigUtils.getProperty("sendcloud.system.apiuser")),
                new NameValuePair("api_key", ConfigUtils.getProperty("sendcloud.apikey")),
                new NameValuePair("from", ConfigUtils.getProperty("sendcloud.system.from")), new NameValuePair("fromname", "小幺鸡系统通知"),
                new NameValuePair("subject", "小幺鸡系统通知-验证码"), new NameValuePair("substitution_vars", vars), new NameValuePair("use_maillist", "false"),
                new NameValuePair("template_invoke_name", "captcha"),};
        String rs = HttpUtils.post(TEMPLATE_URL, pairs);
        if (rs.contains("error")) {
            throw new RuntimeException(rs);
        }
        logger.debug(rs);
    }

    @Override
    public void findPassword(String findPageURL, String to) {
        String vars = JSON.toJSONString(new _HashMap<>().add("to", new String[]{to}).add("sub", new _HashMap<>().add("%url%", new String[]{findPageURL})));
        NameValuePair[] pairs = new NameValuePair[]{new NameValuePair("api_user", ConfigUtils.getProperty("sendcloud.system.apiuser")),
                new NameValuePair("api_key", ConfigUtils.getProperty("sendcloud.apikey")),
                new NameValuePair("from", ConfigUtils.getProperty("sendcloud.system.from")), new NameValuePair("fromname", "小幺鸡系统通知"),
                new NameValuePair("subject", "小幺鸡系统通知-找回密码"), new NameValuePair("substitution_vars", vars),
                new NameValuePair("template_invoke_name", "find_password"),};
        String rs = HttpUtils.post(TEMPLATE_URL, pairs);
        if (rs.contains("error")) {
            throw new RuntimeException(rs);
        }
        logger.debug(rs);
    }
}
