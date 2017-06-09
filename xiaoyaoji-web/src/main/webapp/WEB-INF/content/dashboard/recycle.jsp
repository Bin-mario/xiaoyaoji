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
<div class="home-body" id="appmain">
    <p class="ta-c" style="margin: 50px 0;color: #ccc;">提示：删除的文件保留30天，超过30天会自动删除</p>
    <c:if test="${projects.size() > 0}">
    <div class="mc home-projects">
        <div class="cb home-p-title">
            <div class="col-sm-4">项目名称</div>
            <div class="col-sm-2">拥有者</div>
            <div class="col-sm-2">有效期</div>
            <div class="col-sm-4 p-actions">操作</div>
        </div>
        <c:forEach items="${projects}" var="item">
        <div class="cb">
            <div class="col-sm-4"><a href="${ctx}/project/${item.id}">${item.name} </a></div>
            <div class="col-sm-2">凉粉 </div>
            <div class="col-sm-3">${item.expires} </div>
            <div class="col-sm-3 p-actions">
                <span uk-icon="icon: history" v-on:click="restore('${item.id}')"></span>
                <span uk-icon="icon: trash" v-on:click="deleteActual('${item.id}')"></span>
            </div>
        </div>
        </c:forEach>
    </div>
    </c:if>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script src="${assets}/js/dashboard/home.js"></script>
</body>
</html>