<%--
  User: zhoujingjie
  Date: 17/5/23
  Time: 21:30
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="project-left">
    <ul class="project-menus">
        <li><a href="${ctx}/profile" class="${pageName=='index'?'active':''}">基本信息</a></li>
        <li><a href="${ctx}/profile/relation" class="${pageName=='relation'?'active':''}">账号关联</a></li>
        <li><a href="${ctx}/profile/security" class="${pageName=='security'?'active':''}">安全设置</a></li>
    </ul>
</div>