package cn.xiaoyaoji.plugin.login.weibo;

import cn.com.xiaoyaoji.core.plugin.LoginPlugin;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.data.bean.Thirdparty;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.util.PluginUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public class WeiboLoginPlugin implements LoginPlugin {
    private static Logger logger = Logger.getLogger(WeiboLoginPlugin.class);
    @Override
    public User doRequest(HttpServletRequest request) {
        String uid = request.getParameter("uid");
        String accessToken = request.getParameter("accessToken");
        AssertUtils.notNull(accessToken, "missing accessToken");
        AssertUtils.notNull(uid, "missing uid");
        AssertUtils.notNull(accessToken, "missing accessToken");
        cn.xiaoyaoji.plugin.login.weibo.User weiboUser = new Weibo().showUser(accessToken, uid);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(weiboUser.getId());
        thirdparty.setLogo(weiboUser.getAvatar_large());
        thirdparty.setNickName(weiboUser.getScreen_name());
        thirdparty.setType(Thirdparty.Type.WEIBO);
        User user = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(user,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        return user;
    }

    @Override
    public void callback(String action, PluginInfo<LoginPlugin> pluginInfo,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        logger.info("callback weibo -> code:" + code + " state:" + state);
        if ("login".contains(state)) {
            Weibo weibo = new Weibo();
            cn.xiaoyaoji.plugin.login.weibo.AccessToken accessToken = weibo.getAccessToken(ConfigUtils.getProperty("weibo.appkey"), ConfigUtils.getProperty("weibo.appsecret"), code, ConfigUtils.getProperty("weibo.redirect_uri"));
            request.setAttribute("uid",accessToken.getUid());
            request.setAttribute("type","weibo");
            request.setAttribute("state",state);
            request.setAttribute("accessToken",accessToken.getAccess_token());
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir()+pluginInfo.getRuntimeFolder()+"/web/"+"third-party.jsp").forward(request,response);

        }
    }
}
