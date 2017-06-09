<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:11
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--[if lt IE 9]> <script>location.href='unsupport.html?refer='+encodeURIComponent(location.href)</script> <![endif]-->
<!DOCTYPE html>
<html lang="zh-Hans">
<head>
    <title>小幺鸡，简单好用的接口文档管理工具</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/index.css?v=${v}">
</head>
<body>
<div id="app">
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<section class="m-sec1">
    <div class="m-sec1-desc ta-c">小幺鸡，简单好用的在线接口文档管理工具</div>
    <div class="ta-c m-sec1-go"><a href="dashboard/">立即使用</a></div>
</section>
<section class="m-sec2 cb mc">
    <div class="fl">
        <h3>在线接口测试</h3>
        <p class="m-s-desc">在线测试，方便前后端开发，降低错误率。<br/>支持：xml、json、txt、binary、websocket</p>
    </div>
    <div class="fl">
        <h3>可视化编辑与分享</h3>
        <p class="m-s-desc">可视化编辑器，完善的分享机制，多功能导出。让接口撰写变得十分简单。</p>
    </div>
    <div class="fl">
        <h3>安全保障</h3>
        <p class="m-s-desc">基于阿里云服务器，提供安全备份系统。多家公司使用，安全证明。</p>
    </div>
    <div class="fl">
        <h3>代码开源</h3>
        <p class="m-s-desc">可离线安装到内网服务器仅供公司内部使用。</p>
    </div>
</section>
<section class="m-sec3 ta-c">
    <div class="about-desc">
        <p>如果你喜欢下面这些，那你一定会喜欢上“小幺鸡”</p>
        <div class="about-list mc">
            <p><i class="iconfont icon-yes"></i>接口在线测试，降低接口错误率</p>
            <p><i class="iconfont icon-yes"></i>开放源码，支持任意修改</p>
            <p><i class="iconfont icon-yes"></i>简洁明了的API</p>
            <p><i class="iconfont icon-yes"></i>简单的维护更新</p>
            <p><i class="iconfont icon-yes"></i>多种导出满足不同需求</p>
            <p class="m-sec1-go"><a href="dashboard/">立即使用</a></p>
        </div>
    </div>
</section>
<section class="m-sec-contact">
    <ul class="cb mc">
        <li>
            <p><i class="iconfont icon-qqqun"></i></p>
            <p>群:<a href="http://shang.qq.com/wpa/qunwpa?idkey=7e99bd0ada4c6586d8e4e609b28d997f86e07336124fca08ac7b02fbe9d07130">580084426</a> </p>
        </li>
        <li>
            <p><i class="iconfont icon-weibo"></i></p>
            <p><a href="http://weibo.com/2727734575" target="_blank">iAm凉粉</a> </p>
        </li>
        <li>
            <p><i class="iconfont icon-email"></i></p>
            <p><a href="mailto:cn_bboy@163.com">cn_bboy@163.com</a> </p>
        </li>

    </ul>
</section>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>

</body>
</html>