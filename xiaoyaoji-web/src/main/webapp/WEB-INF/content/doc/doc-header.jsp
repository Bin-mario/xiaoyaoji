<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/27
  Time: 22:06
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-Hans">
<head>
    <title>${doc.name}-${project.name}</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${assets}/css/style.css">
    <link rel="stylesheet" href="${assets}/css/doc.css">
    <link rel="stylesheet" href="${assets}/css/icons.css">
    <link rel="stylesheet" href="${assets}/css/app.css">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit-rtl.css">
    <link rel="stylesheet" href="${assets}/uikit/css/uikit.css">
    <link rel="alternate" hreflang="zh-hans" href="//www.xiaoyaoji.com.cn/"/>
    <meta name="keywords"
          content="小幺鸡,接口文档管理,接口平台,在线文档,api管理,api测试,接口文档工具,接口演示,rest,restful,rest api,接口测试,postman,文档管理,websocket在线测试"/>
    <meta name="description" content="简单好用的在线文档管理助手，支持restapi、websocket在线测试,支持markdown编辑器,提升开发效率降低接口错误率。"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="shortcut icon" type="image/x-ico" href="${ctx}/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="renderer" content="webkit"/>
    <jsp:include page="/WEB-INF/includes/js.jsp"/>
</head>
<body>
<div class="doc">
    <div class="doc-header cb" id="doc-header">
        <c:if test="${edit}">
        <div class="fl doc-header-return">
            <a href="${ctx}/dashboard"><span class="uk-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="40"
                                                                  height="40" viewBox="0 0 20 20" ratio="2"><polyline
                    fill="none" stroke="#000" stroke-width="1.03" points="13 16 7 10 13 4"></polyline></svg></span></a>
        </div>
        <div class="fl">
            <input placeholder="请输入项目名称" autofocus debounce="500" v-model="projectName" maxlength="30"
                   class="project-name" type="text" value="${project.name}">
        </div>
        </c:if>
        <c:if test="${!edit}">
            <div class="fl project-name">${project.name}</div>
        </c:if>
        <c:if test="${sessionScope.user!=null}">
        <div class="fr">
            <ul class="cb">
                <c:if test="${docId != null && edit}">
                    <li class="fl"><a href="${ctx}/doc/${docId}"><i class="iconfont icon-yulan"></i>预览文档</a></li>
                </c:if>
                <c:if test="${!edit && editPermission}">
                    <li class="fl"><a href="${ctx}/doc/${docId}/edit"><i class="iconfont icon-yulan"></i>编辑文档</a></li>
                </c:if>
                <c:if test="${edit}">
                    <li class="fl" uk-toggle="target: #save-desc"><a><i class="iconfont icon-yulan"></i>保存</a></li>
                    <li class="fl"><a href="${ctx}/project/global/${project.id}"><i class="iconfont icon-yulan"></i>全局设置</a>
                    </li>
                </c:if>
                <c:if test="${editPermission}">
                <li id="doc-history-li" class="fl" v-cloak v-show="history.length>0"><a href=""><i class="el-icon-setting"></i>历史版本</a></li>
                <div uk-dropdown v-show="history.length>0" v-cloak class="doc-history">
                    <table class="uk-table">
                        <caption>历史版本</caption>
                        <thead>
                        <tr>
                            <th>版本号</th>
                            <th>修改说明</th>
                            <th>修改人</th>
                            <th>修改时间</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="item in history">
                            <td><a :href="historyURL(item.docId,'${edit}',item.id)">{{item.id}}</a></td>
                            <td class="doc-history-comment" :title="item.comment">{{item.comment?item.comment:'无'}}</td>
                            <td>{{item.userName}}</td>
                            <td>{{item.createTime}}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                </c:if>
                <c:if test="${project.userId == user.id}">
                    <li class="fl"><a href="${ctx}/project/${project.id}/info"><i class="el-icon-setting"></i>项目设置</a></li>
                </c:if>
                <li class="fl"><a href="${ctx}/dashboard"><i class="el-icon-menu"></i>控制台</a></li>
                <li class="fl"><a href="${ctx}/profile">个人中心</a></li>
            </ul>

            <!-- This is the modal -->
            <div id="save-desc" uk-modal>
                <div class="uk-modal-dialog uk-modal-body">
                    <h2 class="uk-modal-title">保存文档</h2>
                    <p class="uk-margin"><textarea autofocus class="uk-textarea" rows="5" v-model="submitComment"
                                                   placeholder="可以在这儿输入一些备注"></textarea></p>
                    <p style="color: rgb(204, 204, 204);">提示:CTRL+S 可快速保存</p>
                    <p class="uk-text-right">
                        <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                        <button class="uk-button uk-button-primary uk-modal-close" type="button" v-on:click="submit">
                            保存
                        </button>
                    </p>
                </div>
            </div>
        </div>
        </c:if>
        <c:if test="${sessionScope.user == null}">
            <div class="fr">
                <ul class="cb">
                    <li class="fl"><a href="${ctx}/">主页</a></li>
                    <li class="fl"><a href="${ctx}/login">登录</a></li>
                    <li class="fl"><a href="${ctx}/register">注册</a></li>
                </ul>
            </div>
        </c:if>
    </div>
    <div class="uk-sticky-placeholder"></div>
    <script>window._isGlobal_ = '${editProjectGlobal}'</script>