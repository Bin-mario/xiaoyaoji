<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                <li onclick="location.href='${ctx}/project/${project.id}/export/pdf'" id="export-pdf"><i class="iconfont icon-pdf"></i>
                    <p>导出PDF</p></li>
                <%--<li v-on:click="json"><i class="iconfont icon-jsonfile"></i>
                    <p>导出JSON</p></li>--%>
                <!--<li><i class="iconfont icon-sql"></i> <p>导出SQL</p></li>-->
            </ul>
        </div>
    </div>
</div>
