<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:15
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]>
<script>location.href = '../unsupport.html'</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>回收站 - ${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body class="home">
<jsp:include page="header.jsp"/>
<div class="home-body" id="appmain" v-cloak>
    <div class="spinner" v-show="loading.project">
        <div class="double-bounce1"></div>
        <div class="double-bounce2"></div>
    </div>
    <div class="mc home-projects" v-if="projects.length>0">
        <div class="cb home-p-title">
            <div class="col-sm-4">项目名称</div>
            <div class="col-sm-2">拥有者</div>
            <div class="col-sm-6 p-actions">操作</div>
        </div>
        <div class="cb" v-for="item in projects">
            <div class="col-sm-4"><a :href="'${ctx}/project/'+item.id">&nbsp;{{item.name}} </a></div>
            <div class="col-sm-2">凉粉 </div>
            <div class="col-sm-6 p-actions">
                <i class="iconfont icon-history" v-on:click="restore(item.id)"></i>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>_userId_='${user.id}',status='ARCHIVE';</script>
<script src="${assets}/js/dashboard/home.js?v=${v}"></script>
</body>
</html>