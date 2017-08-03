<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="cn.com.xiaoyaoji.core.plugin.PluginManager" %>
<%@ page import="cn.com.xiaoyaoji.core.plugin.Event" %><%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setAttribute("exportPlugins",PluginManager.getInstance().getPlugins(Event.DOC_EXPORT));
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>导出-${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body>
<jsp:include page="../dashboard/header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content">
        <div class="db-export">
            <ul class="cb">
                <c:forEach items="${exportPlugins}" var="item">
                    <li onclick="window.open('${ctx}/project/${project.id}/export/${item.id}/do')">
                        <img src="${ctx}/proxy/${item.id}/${item.icon.icon32x32}?v=${item.version}">
                        <p>${item.name}</p>
                    </li>
                </c:forEach>

            </ul>
        </div>
    </div>
</div>
