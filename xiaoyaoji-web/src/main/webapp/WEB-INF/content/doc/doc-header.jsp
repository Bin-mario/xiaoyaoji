<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/27
  Time: 22:06
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-Hans">
<head>
    <title>${doc.name}-${project.name}</title>
    <meta charset="UTF-8">
    <%--<link rel="stylesheet" href="${assets}/css/style.css">--%>
    <link rel="stylesheet" href="${assets}/css/doc.css">
    <link rel="stylesheet" href="${assets}/css/icons.css">
    <link rel="stylesheet" href="${assets}/css/app.css">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit-rtl.css">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit.css">
    <link rel="alternate" hreflang="zh-hans" href="//www.xiaoyaoji.cn/"/>
    <meta name="keywords"
          content="小幺鸡,接口文档管理,接口平台,在线文档,api管理,api测试,接口文档工具,接口演示,rest,restful,rest api,接口测试,postman,文档管理,websocket在线测试"/>
    <meta name="description" content="简单好用的在线文档管理助手，支持restapi、websocket在线测试,支持markdown编辑器,提升开发效率降低接口错误率。"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="shortcut icon" type="image/x-ico" href="${ctx}/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="renderer" content="webkit"/>
    <jsp:include page="/WEB-INF/includes/js.jsp"/>
</head>
<body>
<div class="doc">
    <script>window._isGlobal_ = '${editProjectGlobal}'</script>
    <jsp:include page="doc-sidebar.jsp"/>
