<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${assets}/css/http.css?v=${v}"/>
<div class="content-section" id="docApp" style="padding: 0 10px;">

<span class="doc-update-time">更新时间: <span id="api-update-time">{{doc.lastUpdateTime}}</span></span>
    <div id="api-details" class="api-details">
        <p class="doc-item-section-title">基本信息</p>
        <div class="api-base-info api-edit-box doc-item-section">
            <div class="cb">
                <div class="col-sm-3">接口名称: ${doc.name}</div>
                <div class="col-sm-6">接口地址: {{requestURL}}</div>
                <div class="col-sm-3">接口状态: {{content.status}}</div>
            </div>
        </div>
        <div class="doc-item-section" v-if="content.description">
            <p class="doc-item-section-title">接口描述</p>
            <div v-html="content.description"></div>
        </div>
        <%--<c:if test="${doc.attachs.size() > 0}">--%>
        <div class="doc-item-section" v-if="attachs && attachs.length>0">
            <p class="doc-item-section-title">附件</p>
            <div class="cb">
                <div class="doc-attach" v-for="item in attachs" v-bind:class="{'file':item.type=='FILE'}">
                    <a :href="fileAccess+item.url" v-if="item.type=='FILE'" target="_blank">{{item.fileName}}</a>
                    <img v-if="item.type =='IMG'" v-bind:src="fileAccess+item.url" :onclick="'window.open('+fileAccess+item.url+');'">
                </div>
            </div>
        </div>
<%--
        <div v-if=" (global.requestArgs  && global.requestArgs.length>0)">
            <p class="doc-item-section-title">全局请求参数</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-1">类型</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-6">描述</li>
                </ul>
                <request-args-vue
                        v-bind:request-args.sync="global.requestArgs"></request-args-vue>
            </div>
        </div>
        <div v-if="(global.http.responseArgs  && global.http.responseArgs.length>0)">
            <p class="doc-item-section-title">全局响应数据</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-3">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">数据类型</li>
                    <li class="col-sm-6">描述</li>
                </ul>
                <response-args-vue
                        v-bind:response-args.sync="global.http.responseArgs"></response-args-vue>
            </div>
        </div>
        <div v-if="(content.http.requestHeaders  && content.http.requestHeaders.length>0)">
            <p class="doc-item-section-title">请求头</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-7">描述</li>
                </ul>
                <request-headers-vue
                        v-bind:request-headers.sync="content.http.requestHeaders"></request-headers-vue>
            </div>
        </div>--%>

        <div v-if=" (content.requestArgs  && content.requestArgs.length>0)">
            <p class="doc-item-section-title">请求参数</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-1">类型</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-6">描述</li>
                </ul>
                <request-args-vue
                        v-bind:request-args.sync="content.requestArgs"></request-args-vue>
            </div>
        </div>
         <div v-if="content.responseArgs && content.responseArgs.length>0">
            <p class="doc-item-section-title">响应数据</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-3">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">数据类型</li>
                    <li class="col-sm-6">描述</li>
                </ul>
                <response-args-vue
                        v-bind:response-args.sync="content.responseArgs"></response-args-vue>
            </div>
        </div>


        <div v-if="content.example">
            <p class="doc-item-section-title">例子</p>
            <div class="api-details-desc api-edit-box">
                <pre class="content">{{content.example}}</pre>
            </div>
        </div>
        <p class="doc-item-section-title">演示</p>
        <div>
            <div class="form">
                <div class="item">
                    <div class="col-sm-2 label second">请求地址</div>
                    <div class="col-sm-8">
                        <input type="hidden" :value="requestURL">
                        <input type="text" class="text" id="requestURL">
                    </div>
                    <div class="col-sm-2" v-if="global.environment.length>0">
                        <div class="uk-button-group">
                            <button class="uk-button uk-button-default">{{currentEnv.name}}</button>
                            <div class="uk-inline">
                                <button class="uk-button uk-button-default" type="button"><span uk-icon="icon:  triangle-down"></span></button>
                                <div uk-dropdown="mode: click; boundary: ! .uk-button-group; boundary-align: true;">
                                    <ul class="uk-nav uk-dropdown-nav">
                                        <li v-for="item in global.environment" v-on:click="currentEnv=item" v-bind:class="{'uk-active':item.t == currentEnv.t}"><a href="#">{{item.name}}</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form">
                <div v-if="urlArgs.length>0">
                    <p class="doc-item-section-title second">地址参数</p>
                    <div class="item" v-for="item in urlArgs">
                        <div class="col-sm-2 label">{{item.name}}</div>
                        <div class="col-sm-8">
                            <input data-type="text" v-model="item.value" type="text"
                                   :name="item" placeholder="替换URL上的参数"
                                   class="text">
                        </div>
                    </div>
                </div>
            </div>

            <div class="form">
                <div class="item">
                    <div class="col-sm-2 label">日志</div>
                    <div class="col-sm-10">
                        <textarea class="text" v-model="ws.log"
                                                     rows="10"></textarea></div>
                </div>
                <div class="item" v-if="ws.connected">
                    <div class="col-sm-2 label">发送消息</div>
                    <div class="col-sm-10"><input type="text" class="text" v-model="ws.message">
                    </div>
                </div>

                <div class="item">
                    <div class="col-sm-2">&nbsp;</div>
                    <div class="col-sm-10">
                        <input type="button" v-if="!ws.connected" v-on:click="wsConnect"
                               class="btn btn-primary" value="连接">
                        <template v-else>
                            <input type="button" class="btn btn-primary"
                                   v-on:click="wsSendMessage" value="发送消息">
                            <input type="button" class="btn btn-danger"
                                   v-on:click="wsDisconnect" value="断开连接">
                        </template>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<jsp:include page="../../includes/doc/table/request-args.jsp"/>
<jsp:include page="../../includes/doc/table/response-args.jsp"/>
<script>
    var doc = ${doc},projectGlobal=${projectGlobal};
</script>
<script src="${ctx}/plugin?plugin=/${doc.type}/view.js"></script>