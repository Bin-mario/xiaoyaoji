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
            <c:if test="${share == null}">
            <ul class="ta-c">
                <%--<li class="db-item"><a href="#!/add"><i class="iconfont icon-add-circle" style="font-weight: bold;"></i></a>
                </li>--%>

                <li class="db-item" v-on:click="loadHistory">
                    <a title="历史版本"><i class="iconfont icon-history"></i></a>
                </li>
                <li class="db-item ">
                    <a title="全局设置"><i
                        class="iconfont icon-global"></i></a>
                    <ul class="sub-ul">
                        <li class="db-item" v-on:click="loadGlobal('http')" ><a>全局参数</a></li>
                        <li class="db-item" v-on:click="loadGlobal('env')" ><a>环境变量</a></li>
                        <li class="db-item" v-on:click="loadGlobal('status')"><a>状态名称</a></li>
                    </ul>
                </li>
                <li class="db-item ">
                    <a  title="项目设置"><i class="iconfont icon-dashboard"></i></a>
                    <ul class="sub-ul">
                        <li class="db-item" v-on:click="loadShares"><a>项目分享</a></li>
                        <c:if test="${editPermission}">
                            <li class="db-item"><a href="${ctx}/project/${project.id}/info">项目信息</a></li>
                            <li class="db-item"><a href="${ctx}/project/${project.id}/transfer">项目转让</a></li>
                        </c:if>
                        <li class="db-item"><a href="${ctx}/project/${project.id}/member">项目成员</a></li>
                        <li class="db-item"><a href="${ctx}/project/${project.id}/export">导出项目</a></li>
                        <li class="db-item"><a href="${ctx}/project/${project.id}/quit">退出项目</a></li>
                    </ul>
                </li>
                <c:if test="${docId != null && edit}">
                    <li class="db-item" uk-toggle="target: #save-modal"><a title="保存"><i class="iconfont icon-save"></i></a></li>
                </c:if>
            </ul></c:if>
            <ul class="sidebar-o-op ta-c">
                <li class="db-item "><a href="${ctx}/profile" title="个人中心"><i class="iconfont icon-user"></i></a></li>
                <li class="db-item" v-on:click="showProject"><a title="项目列表"><i
                        class="iconfont icon-projects active"></i></a></li>
            </ul>
        </div>
        <div class="db-left-layer hide" onclick="$('#sidebar').removeClass('layer')"></div>
    </div>

   <%-- <div class="api-modules-tab ta-c">
        <c:if test="${docId != null && editPermission}">
            <a class="api-module api-module-item ${edit?'active':''}" v-on:click="editpage">编辑模式</a>
        </c:if>
        <c:if test="${editPermission}">
            <a class="api-module api-module-item ${!edit?'active':''}" v-on:click="viewpage">浏览模式</a>
        </c:if>
    </div>
--%>
    <div id="history-modal" uk-modal class="uk-modal-container">
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

    <div id="save-modal" uk-modal>
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


    <div id="share-modal" uk-modal v-cloak>
        <div class="uk-modal-dialog">
            <div class="uk-modal-header">
                <h2 class="uk-modal-title">{{shareBox=='list'?'分享列表':'创建新分享'}}</h2>
            </div>

            <div class="uk-modal-body" uk-overflow-auto>

                <!-- 分享列表 begin -->
                <div class="share-list-box " v-show="shareBox=='list'">
                    <ul class="share-list">
                        <div class="spinner" v-show="loading.share">
                            <div class="double-bounce1"></div>
                            <div class="double-bounce2"></div>
                        </div>
                        <li v-for="item in shares" v-bind:class="{editing:item.editing}">
                            <div class="cb">
                                <a class="share-name fl" target="_blank" v-bind:href="'${ctx}/share/'+item.id">[{{item.username}}] {{item.name}} </a>
                                <c:if test="${editPermission}">
                                <div class="fr">
                                    <i class="iconfont icon-lock" v-on:click="item.editing=true;"></i>
                                    <i class="iconfont icon-close" v-on:click="deleteShare(item)"></i>
                                </div>
                                </c:if>
                                <input type="text" class="uk-input fr" v-bind:autofocus="item.editing" v-on:blur="shareLockBlur(item)" v-model="item.password" v-bind:value="item.password" placeholder="为空则表示不要密码">
                            </div>
                            <div class="cb">
                                <div class="fl share-doc-names">{{item.shareAll=='YES'?'整个项目':item.docNames}} {{item.docNames}}{{item.docNames}}{{item.docNames}}</div>
                                <div class="fr">{{item.createTime}}</div>
                            </div>
                        </li>
                    </ul>
               </div>
                <!-- 分享列表 end -->

                <!-- 创建新的分享 begin -->
                <div class="share-creation-box" v-show="shareBox=='creation'">
                    <form class="uk-form-horizontal uk-margin-large">
                        <div class="uk-margin">
                            <label class="uk-form-label" for="form-all-project">整个项目</label>
                            <div class="uk-form-controls">
                                <input class="uk-checkbox" id="form-all-project" v-model="share.shareAll" v-bind:true-value="'YES'" v-bind:false-value="'NO'"  type="checkbox">
                            </div>
                        </div>

                        <div class="uk-margin" v-show="share.shareAll!='YES'">
                            <label class="uk-form-label">选择分类</label>
                            <div class="uk-form-controls">
                                <select class="uk-select" multiple  v-model="share.chosedIds">
                                    <option v-for="item in rootDocs" v-bind:value="item.id">{{item.name}}</option>
                                </select>
                            </div>
                        </div>
                        <div class="uk-margin">
                            <label class="uk-form-label">分享名称</label>
                            <div class="uk-form-controls">
                                <input class="uk-input" maxlength="50" v-model="share.name" type="text" placeholder="请输入分享名称">
                            </div>
                        </div>
                        <div class="uk-margin">
                            <label class="uk-form-label">阅读密码</label>
                            <div class="uk-form-controls">
                                <input class="uk-input" maxlength="20" v-model="share.password" type="text" placeholder="请输入阅读密码，为空表示不要密码">
                            </div>
                        </div>
                    </form>
                </div>
                <!-- 创建新的分享 end -->
            </div>

            <div class="uk-modal-footer uk-text-right">
                <div v-show="shareBox=='list'">
                    <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                    <c:if test="${editPermission}">
                    <button class="uk-button uk-button-primary" type="button" v-on:click="shareBox='creation'">创建新分享</button>
                    </c:if>
                </div>
                <div v-show="shareBox=='creation'">
                    <button class="uk-button uk-button-default" v-on:click="shareBox='list'" type="button">返回</button>
                    <button class="uk-button uk-button-primary" v-on:click="createShare" type="button">创建</button>
                </div>
            </div>

        </div>
    </div>

    <div id="global-modal" class="uk-modal-container"  uk-modal v-cloak>
        <div class="uk-modal-dialog">
            <div class="uk-modal-header">
                <h2 class="uk-modal-title">{{global.typeName}}</h2>
                <button type="button" class="uk-modal-close-default uk-icon" v-on:click="closeGlobalModal">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 14 14" ratio="1"><line fill="none" stroke="#000" stroke-width="1.1" x1="1" y1="1" x2="13" y2="13"></line><line fill="none" stroke="#000" stroke-width="1.1" x1="13" y1="1" x2="1" y2="13"></line></svg>
                </button>
            </div>

            <div class="uk-modal-body" uk-overflow-auto>
                <iframe width="100%" name="globalWindow" id="iframe" height="400px" marginwidth="0"
                        marginheight="0" allowtransparency="true" v-bind:src="global.url"></iframe>
            </div>

            <div class="uk-modal-footer uk-text-right" v-if="global.typeName=='全局参数'">
                <div>
                    <button class="uk-button uk-button-default" v-on:click="closeGlobalModal" type="button">关闭</button>
                    <button class="uk-button uk-button-primary" v-on:click="saveGlobalHttp" type="button">保存</button>
                </div>
            </div>
        </div>
    </div>





</div>




<script src="${assets}/js/project/sidebar.js?v=${v}"></script>