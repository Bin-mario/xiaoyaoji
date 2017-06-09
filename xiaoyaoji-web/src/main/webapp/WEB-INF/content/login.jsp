<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:12
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]> <script>location.href='unsupport.html?refer='+encodeURIComponent(location.href)</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>登录 - 小幺鸡</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/index.css?v=${v}">
</head>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<body class="login-register-body">
<div class="login-form mc" id="login">
    <form class="form" v-on:submit.prevent>
        <div class="ta-c logo"><a href="${ctx}/"><img src="${assets}/img/logo/full.png"></a></div>
        <div class="item">
            <input type="text" initial="off" name="email" v-model="email" id="email" tabindex="1"
                   maxlength="45" v-validate="'required|email'" class="text" placeholder="邮箱"/>
            <p v-cloak class="tip">{{ errors.first('email') }}</p>
        </div>
        <div class="item">
            <input type="password" name="password" initial="off" tabindex="2" v-model="password"
                   v-validate="'required'" class="text" placeholder="密码" :class="{'invalid':errors.has('password')}"/>
            <p v-cloak class="tip" v-show="errors.has('password')">{{ errors.first('password') }}</p>
        </div>
        <div class="item">
            <input type="submit" v-on:click="submit" tabindex="3" id="login-btn" class="btn" value="登陆"/>
        </div>
        <div class="item">
            <div class="col-sm-8">
                <a href="forget">忘记密码</a>
                <a href="register">免费注册</a>
            </div>
            <div class="col-sm-4">
                <label><input v-model="remember" type="checkbox"> 记住密码</label>
            </div>
        </div>

    </form>
    <div class="long-line"></div>
    <div class="login-third ta-c">
        <a v-on:click="qq"><i class="iconfont icon-qq"></i></a>
        <a v-on:click="weibo"><i class="iconfont icon-weibo"></i></a>
        <a v-on:click="github"><i class="iconfont icon-github"></i></a>
    </div>
    <br/>
</div>

<script src="${assets}/js/login.js"></script>
</body>
</html>