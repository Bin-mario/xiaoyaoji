<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:09
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header>
    <div class="top">
        <div class="cb mc">
            <c:if test="${sessionScope.user!=null}">
                <div class="user-action logged fr">
                    <a class="item" href="${ctx}/dashboard?v=${v}">进入工作台</a>
                    <a class="item" href="${ctx}/logout?t=<%=System.currentTimeMillis()%>">退出</a>
                </div>
            </c:if>
            <c:if test="${sessionScope.user == null}">
                <div class="user-action fr">
                    <a class="item" href="${ctx}/login">登陆</a>
                    <a class="item" href="${ctx}/register">注册</a>
                </div>
            </c:if>
        </div>
    </div>

    <div class="header mc cb">
        <a href="${ctx}/" class="logo fl"><img alt="小幺鸡" src="${assets}/img/logo/full.png"></a>
        <nav class="fl">
            <ul class="cb">
                <li><a href="${ctx}/">主页</a> </li>
                <li><a href="http://www.xiaoyaoji.cn/project/demo" target="_blank">在线演示</a> </li>
                <li><a href="http://git.oschina.net/zhoujingjie/apiManager" rel="nofollow" target="_blank">下载源码</a></li>
                <li><a href="http://git.oschina.net/zhoujingjie/apiManager/releases" rel="nofollow" target="_blank">离线部署</a></li>
                <li><a href="${ctx}/help">在线帮助</a> </li>
            </ul>
        </nav>
    </div>
</header>