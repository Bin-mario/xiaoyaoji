package cn.com.xiaoyaoji.extension.email;

import cn.com.xiaoyaoji.core.util.ConfigUtils;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于commons email 实现
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class DefaultEMailProvider implements EmailProvider {
    private static Logger logger = LoggerFactory.getLogger(DefaultEMailProvider.class);
    private String hostName = ConfigUtils.getProperty("email.smtp.server");
    private int port = Integer.parseInt(ConfigUtils.getProperty("email.smtp.port"));
    private String username = ConfigUtils.getProperty("email.username");
    private String password = ConfigUtils.getProperty("email.password");
    private String from = ConfigUtils.getProperty("email.from");
    @Override
    public void sendCaptcha(String code, String to) {
        try {
            Email email = new SimpleEmail();
            authentication(email);
            email.setFrom(from);
            email.setCharset("UTF-8");
            email.setSubject("小幺鸡验证码");
            email.setMsg("验证码是："+code);
            email.addTo(to);
            email.send();
        } catch (EmailException e) {
            logger.error(e.getMessage(),e);
        }
    }
    private void authentication(Email email){
        email.setHostName(hostName);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setSSLOnConnect(true);
    }

    @Override
    public void findPassword(String findPageURL, String to) {
        try {
            HtmlEmail email = new HtmlEmail();
            authentication(email);
            email.setCharset("UTF-8");
            email.addTo(to);
            email.setFrom(from, "系统管理员");
            email.setSubject("找回密码");
            email.setHtmlMsg("<html><body><a href=\""+findPageURL+"\">点击找回密码</body></html>");
            email.setTextMsg("复制地址到浏览器上打开:"+findPageURL);
            email.send();
        } catch (EmailException e) {
            logger.error(e.getMessage(),e);
        }
    }
}
