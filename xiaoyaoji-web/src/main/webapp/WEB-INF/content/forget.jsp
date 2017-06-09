<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:06
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]> <script>location.href='unsupport.html?refer='+encodeURIComponent(location.href)</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh">
<head>
    <title>忘记密码 - 小幺鸡</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/index.css?v=${v}">
</head>
<body>
<div id="app">
    <div class="login-form mc" v-bind:class="{'succeed':succeed}">
        <form class="form" v-on:submit.prevent id="login-box">
            <div class="ta-c logo"><a href="${ctx}/"><img src="${assets}/img/logo/full.png"></a></div>
            <div v-if="!succeed">
                <div class="item">
                    <input type="text" autofocus="autofocus" name="email" v-model="email" v-validate="'required|email'"
                           class="text" placeholder="请输入注册邮箱"/>
                    <p class="tip">{{errors.first('email')}}</p>
                </div>
                <div class="item">
                    <input type="submit" @click="findPassword" class="btn" value="找回密码"/>
                </div>
            </div>
            <template v-else="succeed">
                <div class="forget-succeed ta-c">
                    <strong>Hi, {{email}}</strong>
                    <p>验证邮件发送成功。</p>
                    <p>请至邮箱查收验证邮件，进行邮箱确认操作。</p>
                    <div class="item"><br/>
                        <a v-on:click="go" class="btn btn-primary lf-gotomail">立刻登陆邮箱完成验证</a>
                    </div>
                </div>
            </template>
        </form>
    </div>

    <div v-if="succeed" v-cloak class="ta-c lr-friendly-tip">若您没有收到邮件您可以：检查您的垃圾邮件中，是否包含验证邮件；或者 <a @click="resend"
                                                                                                  v-text="resendtext"></a>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script src="${assets}/js/forget.js"></script>

</body>
</html>