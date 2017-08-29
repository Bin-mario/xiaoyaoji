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
    <link rel="stylesheet" href="${assets}/css/doc.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/icons.css?v=${v}">

    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <jsp:include page="/WEB-INF/includes/js.jsp"/>
</head>
<body>
<div class="doc">
    <script>window._isGlobal_ = '${editProjectGlobal}'</script>

