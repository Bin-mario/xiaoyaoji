package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.common.Message;
import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.FindPassword;
import cn.com.xiaoyaoji.data.bean.Thirdparty;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.util.CacheUtils;
import cn.com.xiaoyaoji.extension.email.EMailUtils;
import cn.com.xiaoyaoji.extension.file.FileUtils;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.UserService;
import cn.com.xiaoyaoji.utils.*;
import cn.com.xiaoyaoji.extension.file.MetaData;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author zhoujingjie
 * @date 2016-05-31
 */
@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * 修改
     * @param user 当前登录用户
     * @param updateUser 需要更新的用户信息
     * @return
     */
    @PostMapping()
    public Object update(User user, cn.com.xiaoyaoji.service.domain.User updateUser,
                         @CookieValue(Constants.TOKEN_COOKIE_NAME)String token) {
        User temp= updateUser.toUser();
        temp.setId(user.getId());
        temp.setPassword(null);
        temp.setType(null);
        temp.setStatus(null);

        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, "操作失败");
        user=ServiceFactory.instance().getById(user.getId(),User.class);
        CacheUtils.putUser(token,user);
        return rs;
    }

    @Ignore
    @PostMapping("register")
    public Object create(cn.com.xiaoyaoji.service.domain.User user) {
        user.setEmail(user.getEmail().trim());
        AssertUtils.isTrue(StringUtils.isEmail(user.getEmail()), "请输入有效的邮箱");
        AssertUtils.notNull(user.getPassword(), "请输入密码");
        AssertUtils.notNull(user.getEmail(), "请输入邮箱");
        // 检查账号是否已存在
        AssertUtils.isTrue(!ServiceFactory.instance().checkEmailExists(user.getEmail()), Message.EMAIL_EXISTS);
        user.setPassword(PasswordUtils.password(user.getPassword()));
        user.setType(User.Type.USER);
        user.setCreatetime(new Date());
        user.setId(StringUtils.id());
        user.setAvatar("/assets/img/defaultlogo.jpg");
        user.setStatus(User.Status.PENDING);
        int rs = ServiceFactory.instance().create(user.toUser());
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return true;
    }

    @GetMapping("search")
    public Object search(User user, @RequestParam("key") String key) {
        if (key == null || key.trim().length() == 0)
            return null;
        List<User> users = ServiceFactory.instance().searchUsers(key, user.getId());
        return new _HashMap<>().add("users", users);
    }

    @GetMapping("project_users")
    public Object getAllProjectUsers(User user) {
        List<User> users = ServiceFactory.instance().getAllProjectUsersByUserId(user.getId());
        return new _HashMap<>().add("users", users).add("fileAccess", ConfigUtils.getFileAccessURL());
    }

    /**
     * 找回密码1
     *
     * @return
     */
    @Ignore
    @PostMapping("findpassword")
    public Object findPassword(@RequestParam String email) {
        AssertUtils.notNull(email, "邮箱为空");
        AssertUtils.isTrue(StringUtils.isEmail(email), "邮箱格式错误");
        AssertUtils.isTrue(ServiceFactory.instance().checkEmailExists(email), "邮箱不存在");
        FindPassword fp = new FindPassword();
        fp.setIsUsed(0);
        fp.setEmail(email);
        fp.setCreateTime(new Date());
        fp.setId(StringUtils.id());
        int rs = DataFactory.instance().insert(fp);
        AssertUtils.isTrue(rs > 0, "操作失败");
        EMailUtils.findPassword(fp.getId(), email);
        return rs;
    }

    /**
     * 修改密码
     *
     * @return
     */
    @PostMapping("password")
    public Object updatePassword(User user, @RequestParam String password) {
        User temp = new User();
        temp.setId(user.getId());
        temp.setPassword(PasswordUtils.password(password));
        AssertUtils.notNull(password, "密码为空");
        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, "操作失败");
        return rs;
    }

    /**
     * 找回密码2
     *
     * @return
     */
    @Ignore
    @PostMapping("newpassword")
    public Object newPassword(@RequestParam String email, @RequestParam String id, @RequestParam String password) {
        AssertUtils.notNull(email, "邮箱为空");
        AssertUtils.notNull(id, "无效请求");
        AssertUtils.notNull(password, "密码为空");
        AssertUtils.isTrue(StringUtils.isEmail(email), "邮箱格式错误");
        password = PasswordUtils.password(password);
        int rs = ServiceFactory.instance().findPassword(id, email, password);
        AssertUtils.isTrue(rs > 0, "操作失败");
        return 1;
    }

    /**
     * 发送邮箱验证码
     *
     * @return
     */
    @Ignore
    @PostMapping("email/captcha")
    public Object sendEmailCaptcha(@RequestParam String email, @CookieValue(Constants.TOKEN_COOKIE_NAME)String token) {
        String code = StringUtils.code();
        AssertUtils.notNull(email, "邮箱为空");
        AssertUtils.isTrue(StringUtils.isEmail(email), "邮箱格式错误");
        EMailUtils.sendCaptcha(code, email);
        CacheUtils.put(token, "emailCaptcha", code);
        return true;
    }

    /**
     * 新邮件
     *
     * @return
     */
    @PostMapping("email/new")
    public Object newEmail(@RequestParam String code, @RequestParam String email, @CookieValue(Constants.TOKEN_COOKIE_NAME)String token) {
        AssertUtils.notNull(code, "验证码为空");
        AssertUtils.notNull(email, "邮箱为空");
        AssertUtils.isTrue(StringUtils.isEmail(email), "邮箱格式错误");
        String captcha = (String) CacheUtils.get(token, "emailCaptcha");
        AssertUtils.isTrue(code.equals(captcha), "验证码错误");
        // 检查邮箱是否存在
        AssertUtils.isTrue(!ServiceFactory.instance().checkEmailExists(email), "该邮箱已存在");
        User user = CacheUtils.getUser(token);
        User temp = new User();
        temp.setId(user.getId());
        temp.setEmail(email);
        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, "操作失败");
        CacheUtils.putUser(token, UserService.instance().getUser(user.getId()));
        CacheUtils.remove(token,"emailCaptcha");
        return rs;
    }

    /**
     * 绑定第三方
     *
     * @return
     */
    @PostMapping("bind")
    public Object bind(User user, @RequestParam String accessToken,
                       @RequestParam String type,
                       @RequestParam String thirdpartyId,
                       @CookieValue(Constants.TOKEN_COOKIE_NAME)String token
    ) {

        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setUserId(user.getId());
        thirdparty.setType(type);
        thirdparty.setId(thirdpartyId);
        int rs = ServiceFactory.instance().bindUserWithThirdParty(thirdparty);
        user.getBindingMap().put(type,true);
        AssertUtils.isTrue(rs > 0, "操作失败");
        CacheUtils.putUser(token,user);
        return true;
    }

    @PostMapping("unbind/{pluginId}")
    public Object unbind(User user, @PathVariable("pluginId") String pluginId,@CookieValue(Constants.TOKEN_COOKIE_NAME)String token) {
        int rs = ServiceFactory.instance().unbindUserThirdPartyRelation(user.getId(), pluginId);
        AssertUtils.isTrue(rs > 0, "操作失败");
        CacheUtils.putUser(token,UserService.instance().getUser(user.getId()));
        return new _HashMap<>()
                .add("user", user)
                ;
    }

    @PostMapping("avatar")
    public Object uploadAvatar(User user, @RequestParam("avatar") MultipartFile file, @CookieValue(Constants.TOKEN_COOKIE_NAME)String token) throws IOException {
        String fileAccess = ConfigUtils.getFileAccessURL();

        if (file != null && file.getSize() > 0 && file.getContentType().startsWith("image")) {

            MetaData md = FileUtils.upload(file);
            User temp = new User();
            temp.setAvatar(md.getPath());
            temp.setId(user.getId());
            int rs = ServiceFactory.instance().update(temp);
            AssertUtils.isTrue(rs > 0, "上传失败");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(user.getAvatar()) && user.getAvatar().startsWith(fileAccess)) {
                String url = user.getAvatar().substring(fileAccess.length());
                try {
                    FileUtils.delete(url);
                } catch (IOException e) {
                }
            }

            user.setAvatar(temp.getAvatar());
            CacheUtils.putUser(token, user);
            return new _HashMap<>().add("avatar", ConfigUtils.getFileAccessURL()+user.getAvatar());
        }
        return new Result<>(false, "请上传图片");
    }
}
