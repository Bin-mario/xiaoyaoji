<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:07
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]> <script>location.href='unsupport.html?refer='+encodeURIComponent(location.href)</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>在线帮助 - 小幺鸡</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/index.css?v=${v}">
</head>
<body>
<div id="app">
<jsp:include page="/WEB-INF/includes/header.jsp"/>
</div>

<div class="donate">
    <div class="txt">
        你的支持,是我的荣幸。<br/>
        如果你觉得小幺鸡还不错,可以小小的鼓励一下。<br/>
        捐赠的资金基本上用于带宽升级与服务器的维持。<br/>
        谢谢!<br/>
    </div>
    <img src="${cdn}/donate.webp" alt="捐赠" class="donate-img">
</div>


<jsp:include page="/WEB-INF/includes/footer.jsp"/>

</body>
</html>