package cn.xiaoyaoji.plugin.login.qq;

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
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public class QQLoginPlugin extends LoginPlugin {
    private static Logger logger = Logger.getLogger(QQLoginPlugin.class);
    private QQ qq;

    @Override
    public void init() {
        Map<String,String> config = getPluginInfo().getConfig();
        qq = new QQ(config.get("clientId"),config.get("secret"));
    }

    @Override
    public User doRequest(HttpServletRequest request) {
        String openId = request.getParameter("openId");
        String accessToken = request.getParameter("accessToken");
        AssertUtils.notNull(openId, "missing openId");
        AssertUtils.notNull(accessToken, "missing accessToken");
        UserInfo userInfo = qq.getUserInfo(openId, accessToken);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(openId);
        thirdparty.setLogo(userInfo.getFigureurl_qq_2());
        thirdparty.setNickName(userInfo.getNickname());
        thirdparty.setType(Thirdparty.Type.QQ);
        cn.com.xiaoyaoji.data.bean.User user = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(user,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        return user;
    }

    @Override
    public String getOpenURL() {
        String clientid =  getPluginInfo().getConfig().get("clientId");
        String redirectUri = getPluginInfo().getConfig().get("redirectUri");
        return "https://graph.qq.com/oauth2.0/authorize?response_type=code&state=login&client_id="+clientid+"&redirect_uri="+redirectUri;
    }

    @Override
    public void callback(String action,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        logger.info("callback qq -> code:" + code + " state:" + state);
        if ("login".equals(state)) {
            String redirectUri = getPluginInfo().getConfig().get("redirectUri");
            AccessToken accessToken = qq.getAccessToken(code, redirectUri);
            String openId = qq.getOpenid(accessToken.getAccess_token());
            request.setAttribute("openId",openId);
            request.setAttribute("type","qq");
            request.setAttribute("state",state);
            request.setAttribute("accessToken",accessToken.getAccess_token());
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir()+getPluginInfo().getRuntimeFolder()+"/web/"+"third-party.jsp").forward(request,response);
        }
    }
}
