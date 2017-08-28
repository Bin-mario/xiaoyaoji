<%@ page import="cn.com.xiaoyaoji.core.plugin.PluginManager" %>
<%@ page import="cn.com.xiaoyaoji.core.plugin.Event" %>
<%@ page import="cn.com.xiaoyaoji.core.plugin.PluginInfo" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 13:15
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<PluginInfo> importPlugins= PluginManager.getInstance().getPlugins(Event.DOC_IMPORT);
    request.setAttribute("importPlugins",importPlugins);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>${site.name}</title>
    <jsp:include page="/WEB-INF/includes/meta.jsp"/>
    <link rel="stylesheet" href="${assets}/css/home.css?v=${v}">
</head>
<body class="home">

<jsp:include page="header.jsp"/>
<div id="appmain">
    <div class="home-body" v-cloak>
            <p v-if="projects.length == 0" class="ta-c" style="margin: 50px 0;color: #ccc;">还没有项目,赶快来创建一个吧</p>
            <div class="mc home-projects" v-if="projects.length>0">
                <div class="cb home-p-title">
                    <div class="col-sm-4">项目名称</div>
                    <div class="col-sm-2">拥有者</div>
                    <div class="col-sm-2">修改时间</div>
                    <div class="col-sm-4 p-actions">操作</div>
                </div>
                    <div class="cb" v-for="item in projects">
                        <div class="col-sm-4"><a :href="'${ctx}/project/'+item.id">&nbsp;{{item.name}} </a></div>
                        <div class="col-sm-2">&nbsp;{{item.userName}}</div>
                        <div class="col-sm-3">&nbsp;{{item.createTime}} </div>
                        <div class="col-sm-3 p-actions" v-cloak v-if="item.userId == userId">
                            <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20"
                                       ratio="1"><circle fill="none" stroke="#000" cx="9.997" cy="10" r="3.31"></circle><path
                                    fill="none" stroke="#000"
                                    d="M18.488,12.285 L16.205,16.237 C15.322,15.496 14.185,15.281 13.303,15.791 C12.428,16.289 12.047,17.373 12.246,18.5 L7.735,18.5 C7.938,17.374 7.553,16.299 6.684,15.791 C5.801,15.27 4.655,15.492 3.773,16.237 L1.5,12.285 C2.573,11.871 3.317,10.999 3.317,9.991 C3.305,8.98 2.573,8.121 1.5,7.716 L3.765,3.784 C4.645,4.516 5.794,4.738 6.687,4.232 C7.555,3.722 7.939,2.637 7.735,1.5 L12.263,1.5 C12.072,2.637 12.441,3.71 13.314,4.22 C14.206,4.73 15.343,4.516 16.225,3.794 L18.487,7.714 C17.404,8.117 16.661,8.988 16.67,10.009 C16.672,11.018 17.415,11.88 18.488,12.285 L18.488,12.285 Z"></path></svg></span>
                            <div uk-dropdown="pos: left-top" style="text-align: left">
                                <ul class="uk-nav uk-dropdown-nav">
                                    <li><a :href="'${ctx}/project/'+item.id+'/info'">
                                        <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                                              viewBox="0 0 20 20" ratio="1"><path fill="none" stroke="#000"
                                                                                  d="M18.65,1.68 C18.41,1.45 18.109,1.33 17.81,1.33 C17.499,1.33 17.209,1.45 16.98,1.68 L8.92,9.76 L8,12.33 L10.55,11.41 L18.651,3.34 C19.12,2.87 19.12,2.15 18.65,1.68 L18.65,1.68 L18.65,1.68 Z"></path><polyline
                                                fill="none" stroke="#000"
                                                points="16.5 8.482 16.5 18.5 3.5 18.5 3.5 1.5 14.211 1.5"></polyline></svg></span>编辑</a>
                                    </li>
                                        <%--<li v-on:click="copyProject('${item.id}')"><a><span
                                                uk-icon="icon:copy"></span>复制</a></li>--%>

                                    <li><a :href="'${ctx}/project/'+item.id+'/export'"><span>
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><path fill="none" stroke="#000" stroke-width="1.1" d="M6.5,14.61 L3.75,14.61 C1.96,14.61 0.5,13.17 0.5,11.39 C0.5,9.76 1.72,8.41 3.3,8.2 C3.38,5.31 5.75,3 8.68,3 C11.19,3 13.31,4.71 13.89,7.02 C14.39,6.8 14.93,6.68 15.5,6.68 C17.71,6.68 19.5,8.45 19.5,10.64 C19.5,12.83 17.71,14.6 15.5,14.6 L12.5,14.6"></path><polyline fill="none" stroke="#000" points="11.75 16 9.5 18.25 7.25 16"></polyline><path fill="none" stroke="#000" d="M9.5,18 L9.5,9.5"></path></svg>
                                    </span>导出</a></li>
                                    <li v-on:click="rename(item.id,item.name)"><a>
                                        <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><path fill="none" stroke="#000" d="M17.25,6.01 L7.12,16.1 L3.82,17.2 L5.02,13.9 L15.12,3.88 C15.71,3.29 16.66,3.29 17.25,3.88 C17.83,4.47 17.83,5.42 17.25,6.01 L17.25,6.01 Z"></path><path fill="none" stroke="#000" d="M15.98,7.268 L13.851,5.148"></path></svg></span>重命名</a></li>
                                    <li class="uk-nav-divider"></li>
                                    <li v-on:click="archiveProject(item.id)"><a><span>
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><path fill="none" stroke="#000" d="M17.25,6.01 L7.12,16.1 L3.82,17.2 L5.02,13.9 L15.12,3.88 C15.71,3.29 16.66,3.29 17.25,3.88 C17.83,4.47 17.83,5.42 17.25,6.01 L17.25,6.01 Z"></path><path fill="none" stroke="#000" d="M15.98,7.268 L13.851,5.148"></path></svg>
                                    </span>归档</a>
                                    </li>
                                    <li v-on:click="deleteProject(item.id)"><a><span>
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><polyline fill="none" stroke="#000" points="6.5 3 6.5 1.5 13.5 1.5 13.5 3"></polyline><polyline fill="none" stroke="#000" points="4.5 4 4.5 18.5 15.5 18.5 15.5 4"></polyline><rect x="8" y="7" width="1" height="9"></rect><rect x="11" y="7" width="1" height="9"></rect><rect x="2" y="3" width="16" height="1"></rect></svg>
                                    </span>删除</a>
                                    </li>

                                </ul>
                            </div>
                        </div>
                    </div>
            </div>




        <div class="spinner" v-show="loading.project">
            <div class="double-bounce1"></div>
            <div class="double-bounce2"></div>
        </div>


        <div class="new-project">
            <div class="new-project-btn ta-c">
                <div class="np-btns">
                <div class="new-project-btns">
                    <div class="np-btns-item" uk-toggle="target: #importModal">
                        <span class="np-text">导入/上传</span>
                        <div class="nb-btn" style="background-color: #00bcd5">
                            <span class="uk-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><path fill="none" stroke="#000" stroke-width="1.1" d="M6.5,14.61 L3.75,14.61 C1.96,14.61 0.5,13.17 0.5,11.39 C0.5,9.76 1.72,8.41 3.31,8.2 C3.38,5.31 5.75,3 8.68,3 C11.19,3 13.31,4.71 13.89,7.02 C14.39,6.8 14.93,6.68 15.5,6.68 C17.71,6.68 19.5,8.45 19.5,10.64 C19.5,12.83 17.71,14.6 15.5,14.6 L12.5,14.6"></path><polyline fill="none" stroke="#000" points="7.25 11.75 9.5 9.5 11.75 11.75"></polyline><path fill="none" stroke="#000" d="M9.5,18 L9.5,9.5"></path></svg></span>
                        </div>
                    </div>
                    <div class="np-btns-item" uk-toggle="target: #createModal">
                        <span class="np-text">新增</span>
                        <div class="nb-btn" style="background-color: #f24029">
                            <span class="uk-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" ratio="1"><rect x="9" y="1" width="1" height="17"></rect><rect x="1" y="9" width="17" height="1"></rect></svg></span>
                        </div>
                    </div>
                </div>
                </div>
                <span class="new-project-btn-holder">
                    <b>+</b>
                </span>
            </div>
        </div>
    </div>

    <div id="createModal" v-cloak uk-modal>
        <div class="uk-modal-dialog">

            <div class="uk-modal-header">
                <h2 class="uk-modal-title">创建项目</h2>
            </div>

            <div class="uk-modal-body">
                <form class="uk-form-stacked">
                    <div class="uk-margin">
                        <label class="uk-form-label" for="form-stacked-text">项目名称</label>
                        <div class="uk-form-controls">
                            <input class="uk-input" v-model="name" autofocus id="form-stacked-text" type="text" placeholder="请输入项目名称"
                                   maxlength="50">
                        </div>
                    </div>

                    <div class="uk-margin">
                        <label class="uk-form-label" for="form-stacked-select">项目描述</label>
                        <div class="uk-form-controls">
                        <textarea class="uk-textarea" v-model="description" id="form-stacked-select" rows="5"
                                  placeholder="请输入项目描述" maxlength="300"></textarea>
                        </div>
                    </div>

                    <div class="uk-margin uk-grid-small uk-child-width-auto" uk-grid>
                        <label><input class="uk-radio" type="radio" v-model="permission" value="PUBLIC" name="permission"
                                      checked> 公开项目</label>
                        <label><input class="uk-radio" type="radio" v-model="permission" value="PRIVATE" name="permission">
                            私有项目</label>
                    </div>
                </form>
            </div>

            <div class="uk-modal-footer uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                <button class="uk-button uk-button-primary" type="button" v-on:click="newProject">创建</button>
            </div>

        </div>
    </div>

    <div id="importModal" v-cloak uk-modal>
        <div class="uk-modal-dialog">

            <div class="uk-modal-header">
                <h2 class="uk-modal-title">导入/上传</h2>
            </div>

            <div class="uk-modal-body">
                <ul class="home-imports">
                    <c:forEach items="${importPlugins}" var="item">
                    <li class="ta-c">
                        <div>
                            <img class="plugin-icon" src="${ctx}/proxy/${item.id}/${item.icon.icon32x32}?v=${item.version}"/><br/>
                            <span>${item.name}</span>
                        </div>
                        <input type="file" v-on:change="importFile('${item.id}',$event)" class="upload">
                    </li>
                    </c:forEach>
                </ul>
                <div class="uk-text-right">
                    <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                </div>
            </div>

        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script>_userId_='${user.id}';</script>
<script src="${assets}/js/dashboard/home.js?v=${v}"></script>

