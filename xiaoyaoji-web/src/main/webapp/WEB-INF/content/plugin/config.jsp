<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 2017/7/31
  Time: 9:49
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>插件配置页</title>
    <meta charset="UTF-8">
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${assets}/css/style.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/doc.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/icons.css?v=${v}">
    <link rel="stylesheet" href="${assets}/css/app.css?v=${v}">
    <link rel="stylesheet" href="${cdn}/assets/uikit/v3.0.0-beta.30/css/uikit-rtl.min.css">
    <link rel="stylesheet" href="${cdn}/assets/uikit/v3.0.0-beta.30/css/uikit.min.css">
</head>
<body>
<div class="cb mc" style="margin-top: 30px;">
    <h1 class="uk-article-title"><a href="?">插件配置</a></h1><br/>
    <div class="fl" style="width: 200px">
        <c:forEach items="${pluginInfos}" var="entry">
            <p>${entry.key}</p>
            <ul class="uk-list uk-list-bullet">
                <c:forEach items="${entry.value}" var="item">
                    <li><a href="${ctx}/plugin/config?id=${item.id}">${item.name}</a></li>
                </c:forEach>
            </ul>
        </c:forEach>
    </div>
    <div class="fl">
        <form method="post"  class="uk-form-horizontal uk-margin-large">
            <input type="hidden" name="pluginId" value="${pluginId}"/>
            <c:forEach items="${config}" var="entry">
                <div class="uk-margin">
                    <label class="uk-form-label" for="form-horizontal-text">${entry.key}</label>
                    <div class="uk-form-controls">
                        <input class="uk-input" style="width: 400px" name="map['${entry.key}']"  id="form-horizontal-text" type="text" placeholder="${entry.key}" value="${entry.value}">
                    </div>
                </div>
            </c:forEach>
            <div>
                <c:if test="${config.size()>0}">
                <input type="submit" class="uk-button uk-button-primary" value="保存(重启后失效)">
                </c:if>
                <c:if test="${pluginId != null}">
                    <a href="?id=${pluginId}&action=reload" class="uk-button uk-button-secondary ">重新加载插件</a>
                    <a href="?id=${pluginId}&action=unload" class="uk-button uk-button-secondary ">卸载插件</a>
                    <a href="?id=${pluginId}&action=delete" class="uk-button uk-button-danger ">删除插件</a>
                </c:if>
            </div>
        </form>
        <h3 class="uk-article-title"><a href="?">上传插件</a></h3><br/>
        <form action="${ctx}/plugin/upload" enctype="multipart/form-data" method="post"  class="uk-form-horizontal uk-margin-large">
            <div class="uk-margin">
                <label class="uk-form-label" for="form-horizontal-text">选择插件</label>
                <div class="uk-form-controls">
                    <input class="uk-input" name="file" accept="application/zip" type="file">
                </div>
            </div>
            <div>
                <input type="submit" class="uk-button uk-button-primary" value="上传">
            </div>
        </form>
    </div>
</div>
</body>
</html>
