package cn.com.xiaoyaoji.extension.email;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public interface EmailProvider {

    /**
     * 发送验证码
     * @param code
     * @param to
     */
    void sendCaptcha(String code,String to);

    /**
     * 找回密码
     * @param findPageURL
     * @param to
     */
    void findPassword(String findPageURL, String to);
}
