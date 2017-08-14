<%--
  User: zhoujingjie
  Date: 2017/8/8
  Time: 17:28
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="left db-left" id="sidebar">
    <div class="db-left-content dlc1 bg hide">
        <div class="ta-c logo"><a href="${ctx}/dashboard/" class="v-link-active"><img
                src="${assets}/img/logo/full.png"></a></div>
        <div class="dbl-projects">
            <%--<div class="db-left-search">
                <div class="cb">
                    <div class="fl"><i class="iconfont icon-sousuo"></i></div>
                    <div class="fl"><input type="text" placeholder="快速查找项目" value=""></div>
                </div>
            </div>
            <div class="line"></div>
            <br>--%>
            <ul>
                <%--<li class="db-item"><a class="bd-add" href="${ctx}/dashboard"> <i class="iconfont icon-add-circle"></i>创建项目</a>
                </li>
                <li class="line"></li>--%>
                <li class="bd-project-title">常用项目</li>

                <li class="db-item" v-for="item in projects" v-if="item.commonlyUsed=='YES'">
                    <a v-bind:href="'${ctx}/project/'+item.id"><i class="iconfont icon-projects"></i>{{item.name}}</a>
                    <a class="shoucang" v-on:click="switchCommonly(item)"><i class="iconfont icon-biaoxingfill"></i></a>
                </li>

                <li class="bd-project-title">我的项目</li>
                <li v-show="loading.project">
                    <div class="spinner">
                        <div class="double-bounce1"></div>
                        <div class="double-bounce2"></div>
                    </div>
                </li>
                <li class="db-item" v-for="item in projects">
                    <a v-bind:href="'${ctx}/project/'+item.id"><i class="iconfont icon-projects"></i>{{item.name}}</a>
                    <a class="shoucang" v-on:click="switchCommonly(item)">
                        <i class="iconfont"
                           v-bind:class="{'icon-biaoxingfill':item.commonlyUsed=='YES','icon-biaoxing':item.commonlyUsed=='NO'}"></i></a>
                </li>

            </ul>
        </div>

    </div>
    <div class="sidebar bg">
        <div class="db-left-bar">
            <div class="logo ta-c"><a href="${ctx}/dashboard" class="v-link-active"><img
                    src="${assets}/img/logo/full-white.png"></a></div>
            <br> <br> <br>
            <ul class="ta-c">
                <%--<li class="db-item"><a href="#!/add"><i class="iconfont icon-add-circle" style="font-weight: bold;"></i></a>
                </li>--%>
                <li class="db-item" v-on:click="loadHistory" uk-toggle="target: #history-modal">
                    <a title="历史版本"><i class="iconfont icon-history"></i></a>
                </li>
                <li class="db-item "><a title="全局设置" href="${ctx}/project/global/${project.id}"><i
                        class="iconfont icon-global"></i></a></li>
                <li class="db-item "><a href="${ctx}/project/${project.id}/info" title="项目设置"><i
                        class="iconfont icon-dashboard"></i></a></li>
                <c:if test="${!edit && editPermission}">
                    <li class="db-item "><a title="编辑文档" v-on:click="editpage"><i class="iconfont icon-edit"></i></a>
                    </li>
                </c:if>
                <c:if test="${docId != null && edit}">
                    <li class="db-item "><a title="预览文档" v-on:click="viewpage"><i class="iconfont icon-eye"></i></a>
                    </li>
                </c:if>
            </ul>
            <ul class="sidebar-o-op ta-c">
                <li class="db-item "><a href="${ctx}/profile" title="个人中心"><i class="iconfont icon-user"></i></a></li>
                <li class="db-item" v-on:click="showProject"><a title="项目列表"><i
                        class="iconfont icon-projects active"></i></a></li>
            </ul>
        </div>
        <div class="db-left-layer hide" onclick="$('#sidebar').removeClass('layer')"></div>
    </div>

    <div class="api-modules-tab ta-c">
        <c:if test="${docId != null && editPermission}">
            <a class="api-module api-module-item ${edit?'active':''}" v-on:click="editpage">编辑模式</a>
        </c:if>
        <c:if test="${editPermission}">
            <a class="api-module api-module-item ${!edit?'active':''}" v-on:click="viewpage">浏览模式</a>
        </c:if>
    </div>

    <div id="history-modal" uk-modal>
        <div class="uk-modal-dialog">
            <div class="uk-modal-header">
                <h2 class="uk-modal-title">历史版本</h2>
            </div>
            <div class="uk-modal-body" uk-overflow-auto>
                <li v-show="loading.history">
                    <div class="spinner">
                        <div class="double-bounce1"></div>
                        <div class="double-bounce2"></div>
                    </div>
                </li>
                <table class="uk-table" v-show="!loading.history">
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


        </div>
    </div>

</div>



<script src="${assets}/js/project/sidebar.js"></script>