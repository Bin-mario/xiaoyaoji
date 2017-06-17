<%--
  User: zhoujingjie
  Date: 2017/5/27
  Time: 16:40
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="home-header">
    <div class="mc cb ta-c">
        <a href="${ctx}/" class="fl"><img src="${assets}/img/logo/full.png" alt="logo"></a>
        <span class="header-titles">
                <a href="${ctx}/dashboard" class="${pageName=='default'?'active':''}">我的</a>
                <a href="${ctx}/dashboard/recycle" class="${pageName=='recycle'?'active':''}">回收站</a>
                <a href="${ctx}/help">常见问题</a>
            </span>
        <div class="user-account fr">
            <a href=""><img src="${fileAccess}${sessionScope.user.avatar}" class="user-account-logo" alt=""><span>${user.nickname}</span></a>
        </div>
        <div v-cloak uk-dropdown="pos: top-right" style="text-align: left">
            <ul class="uk-nav uk-dropdown-nav">
                <li class="uk-active"><a href="${ctx}/dashboard">控制台</a></li>
                <li class="uk-nav-divider"></li>
                <li><a href="${ctx}/profile">个人中心</a></li>
                <li><a href="${ctx}/profile/security">安全设置</a></li>
                <li><a href="${ctx}/help">帮助中心</a></li>
                <li><a href="http://git.oschina.net/zhoujingjie/apiManager" target="_blank">请作者喝咖啡</a></li>
                <li class="uk-nav-divider"></li>
                <li><a href="${ctx}/logout">退出登录</a></li>
            </ul>
        </div>
    </div>
</div>
<div class="line"></div>