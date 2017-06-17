<%--
  User: zhoujingjie
  Date: 2017/5/25
  Time: 17:25
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]>
<script>location.href = '../unsupport.html'</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>小幺鸡</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${assets}/css/style.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/app.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit.min.css?v=${v}">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit-rtl.min.css?v=${v}">
    <link rel="alternate" hreflang="zh-hans" href="//www.xiaoyaoji.com.cn/"/>
    <meta name="keywords"
          content="小幺鸡,接口文档管理,接口平台,api,api管理,api测试,接口文档工具,接口演示,rest,restful,rest api,接口测试,postman,文档管理,websocket在线测试"/>
    <meta name="description" content="简单好用的在线文档管理工具，支持restapi、websocket在线测试,支持markdown编辑器,提升开发效率降低接口错误率。"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="shortcut icon" type="image/x-ico" href="${ctx}/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="renderer" content="webkit"/>
</head>
<body class="home">

<div class="home-header">
    <div class="mc cb ta-c">
        <a href="${ctx}/dashboard" class="fl"><img src="${assets}/img/logo/full.png" alt="logo"></a>
        <div class="user-account fr">
            <a href=""><img src="${fileAccess}${sessionScope.user.avatar}" class="user-account-logo" alt=""><span>${sessionScope.user.nickname}</span></a>
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
<div class="project-line"></div>