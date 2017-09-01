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
    <link rel="stylesheet" href="${assets}/css/doc.css?v=${v}"/>
    <link rel="stylesheet" href="${assets}/css/icons.css?v=${v}"/>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <jsp:include page="/WEB-INF/includes/js.jsp"/>

</head>
<body>
<div class="xd-header" id="xd-header">
    <ul class="x-ul horiz">
        <li>
            <div class="x-li"><a href="">全局设置</a></div>
            <ul>
                <li><div class="x-li"><a href="">全局参数</a></div></li>
                <li><div class="x-li"><a href="">环境变量</a></div></li>
                <li><div class="x-li"><a href="">全局状态</a></div></li>
            </ul>
        </li>
        <li>
            <div class="x-li"><a href="">项目设置</a></div>
            <ul>
                <li><div class="x-li"><a href="">全局参数</a></div></li>
                <li><div class="x-li"><a href="">环境变量</a></div></li>
                <li><div class="x-li"><a href="">全局状态</a></div></li>
            </ul>
        </li>
        <li>
            <div class="x-li"><a href="">历史记录</a></div>
        </li>
        <li>
            <div class="x-li"><a href="">编辑项目</a></div>
        </li>
        <li>
            <div class="x-li"><a href="">预览项目</a></div>
        </li>
        <li>
            <div class="x-li"><a href="">保存</a></div>
        </li>
    </ul>
</div>

<div class="doc">
    <script>window._isGlobal_ = '${editProjectGlobal}'</script>

