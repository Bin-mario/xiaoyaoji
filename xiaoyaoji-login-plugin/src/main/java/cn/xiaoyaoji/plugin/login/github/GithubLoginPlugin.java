package cn.xiaoyaoji.plugin.login.github;

import cn.com.xiaoyaoji.core.plugin.LoginPlugin;
import cn.com.xiaoyaoji.core.util.AssertUtils;
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
public class GithubLoginPlugin extends LoginPlugin {
    private static Logger logger = Logger.getLogger(GithubLoginPlugin.class);
    @Override
    public User doRequest(HttpServletRequest request) {
        String accessToken = request.getParameter("accessToken");
        AssertUtils.notNull(accessToken, "missing accessToken");
        Github github = new Github();
        cn.xiaoyaoji.plugin.login.github.User user = github.getUser(accessToken);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(user.getId());
        thirdparty.setLogo(user.getAvatar_url());
        thirdparty.setNickName(user.getName());
        thirdparty.setType(getPluginInfo().getId());
        thirdparty.setEmail(user.getEmail());
        User loginUser = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(loginUser,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        return loginUser;
    }

    @Override
    public String getOpenURL() {
        String clientid = getPluginInfo().getConfig().get("clientId");
        String redirectUri = getPluginInfo().getConfig().get("redirectUri");
        return "https://github.com/login/oauth/authorize?client_id="+clientid+"&redirect_uri="+redirectUri+"&scope=user&state=login";
    }

    @Override
    public void callback(String action,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        logger.info("callback github -> code:" + code + " state:" + state);
        if ("login".contains(state)) {
            Github github = new Github();
            String clientId = getPluginInfo().getConfig().get("clientId");
            String secret = getPluginInfo().getConfig().get("secret");
            String redirectUri = getPluginInfo().getConfig().get("redirectUri");
            cn.xiaoyaoji.plugin.login.AccessToken accessToken = github.getAccessToken(clientId, secret, code, redirectUri);
            cn.xiaoyaoji.plugin.login.github.User user = github.getUser(accessToken.getAccess_token());

            request.setAttribute("thirdpartyId",user.getId());
            request.setAttribute("type",getPluginInfo().getId());
            request.setAttribute("state",state);
            request.setAttribute("accessToken",accessToken.getAccess_token());
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir()+getPluginInfo().getRuntimeFolder()+"/web/"+"third-party.jsp").forward(request,response);
        }
    }
}
