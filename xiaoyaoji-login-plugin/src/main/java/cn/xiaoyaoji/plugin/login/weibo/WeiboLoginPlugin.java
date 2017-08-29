package cn.xiaoyaoji.plugin.login.weibo;

import cn.com.xiaoyaoji.core.plugin.LoginPlugin;
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
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public class WeiboLoginPlugin extends LoginPlugin {
    private static Logger logger = Logger.getLogger(WeiboLoginPlugin.class);
    @Override
    public User doRequest(HttpServletRequest request) {
        String thirdpartyId = request.getParameter("thirdpartyId");
        String accessToken = request.getParameter("accessToken");
        AssertUtils.notNull(accessToken, "missing accessToken");
        AssertUtils.notNull(thirdpartyId, "missing thirdpartyId");
        AssertUtils.notNull(accessToken, "missing accessToken");
        cn.xiaoyaoji.plugin.login.weibo.User weiboUser = new Weibo().showUser(accessToken, thirdpartyId);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(weiboUser.getId());
        thirdparty.setLogo(weiboUser.getAvatar_large());
        thirdparty.setNickName(weiboUser.getScreen_name());
        thirdparty.setType(getPluginInfo().getId());
        User user = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(user,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        return user;
    }

    @Override
    public String getOpenURL() {
        String clientid = getPluginInfo().getConfig().get("clientId");
        String redirectUri = getPluginInfo().getConfig().get("redirectUri");
        return "https://api.weibo.com/oauth2/authorize?client_id="+clientid+"&state=login&redirect_uri="+redirectUri;
    }

    @Override
    public void callback(String action ,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        logger.info("callback weibo -> code:" + code + " state:" + state);
        if ("login".contains(state)) {
            Weibo weibo = new Weibo();
            Map<String, String> config =  getPluginInfo().getConfig();
            cn.xiaoyaoji.plugin.login.weibo.AccessToken accessToken = weibo.getAccessToken(config.get("clientId"), config.get("secret"), code, config.get("redirectUri"));
            request.setAttribute("thirdpartyId",accessToken.getUid());
            request.setAttribute("state",state);
            request.setAttribute("type",getPluginInfo().getId());
            request.setAttribute("accessToken",accessToken.getAccess_token());
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir()+getPluginInfo().getRuntimeFolder()+"/web/"+"third-party.jsp").forward(request,response);
        }
    }
}
