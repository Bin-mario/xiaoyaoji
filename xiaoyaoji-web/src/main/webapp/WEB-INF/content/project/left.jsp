<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/5/23
  Time: 21:30
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="project-left">
    <ul class="project-menus">
        <c:if test="${project.userId == sessionScope.user.id}">
            <li><a href="${ctx}/project/${project.id}/info" class="${pageName=='info'?'active':''}">项目信息</a></li>
            <%--<li><a href="${ctx}/project/${project.id}/function" class="${pageName=='function'?'active':''}">扩展功能</a></li>--%>
            <li><a href="${ctx}/project/${project.id}/transfer" class="${pageName=='transfer'?'active':''}">项目转让</a>
            </li>
        </c:if>
        <li><a href="${ctx}/project/${project.id}/member" class="${pageName=='member'?'active':''}">项目成员</a></li>
        <li><a href="${ctx}/project/${project.id}/export" class="${pageName=='export'?'active':''}">导出</a></li>
        <%--<li><a href="${ctx}/project/${project.id}/import" class="${pageName=='import'?'active':''}">导入</a></li>--%>
        <li><a href="${ctx}/project/${project.id}/quit" class="${pageName=='quit'?'active':''}">退出项目</a></li>
    </ul>
</div>