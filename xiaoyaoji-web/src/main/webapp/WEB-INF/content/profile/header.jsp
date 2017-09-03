<%--
  User: zhoujingjie
  Date: 2017/5/25
  Time: 17:25
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>小幺鸡</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${assets}/css/style.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
</head>
<body class="home">

<div class="home-header">
    <div class="mc cb ta-c">
        <a href="${ctx}/dashboard" class="fl"><img src="${assets}/img/logo/full.png" alt="logo"></a>
        <div class="user-account fr">
            <a href=""><img src="${sessionScope.user.avatar}" class="user-account-logo" alt=""><span>${sessionScope.user.nickname}</span></a>
        </div>
        <div v-cloak uk-dropdown="pos: top-right" style="text-align: left">
            <ul class="uk-nav uk-dropdown-nav">
                <li class="uk-active"><a href="${ctx}/dashboard">控制台</a></li>
                <li class="uk-nav-divider"></li>
                <li><a href="${ctx}/profile">个人中心</a></li>
                <li><a href="${ctx}/profile/security">安全设置</a></li>
                <li><a href="${ctx}/help">帮助中心</a></li>
                <li><a href="${ctx}/donate" target="_blank">请作者喝咖啡</a></li>
                <li class="uk-nav-divider"></li>
                <li><a href="${ctx}/logout">退出登录</a></li>
            </ul>
        </div>
    </div>
</div>
<div class="project-line"></div>