<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${assets}/css/http.css?v=${v}"/>
<div class="content-section" id="docApp" style="padding: 0 10px;" v-cloak>

<span class="doc-update-time">更新时间: <span id="api-update-time">{{doc.lastUpdateTime}}</span></span>
    <div id="api-details" class="api-details">
        <p class="doc-item-section-title">基本信息</p>
        <div class="api-base-info api-edit-box doc-item-section">
            <div class="cb">
                <div class="col-sm-3">接口名称: ${doc.name}</div>
                <div class="col-sm-9" v-show="requestURL">接口地址: {{requestURL}}</div>
            </div>
            <div class="cb">
                <div class="col-sm-3" v-if="content.requestMethod">请求方法: {{content.requestMethod}}</div>
                <div class="col-sm-3" v-if="content.dataType">数据类型: {{content.dataType}}</div>
                <div class="col-sm-3" v-if="content.contentType">响应类型: {{content.contentType}}</div>
                <div class="col-sm-3" v-if="content.status">接口状态: {{content.status}}</div>
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
        <%--</c:if>--%>
        <div v-if="(global.http.requestHeaders&&global.http.requestHeaders.length>0)">
            <p class="doc-item-section-title">全局请求头</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-7">描述</li>
                </ul>
                <request-headers-vue
                        v-bind:request-headers.sync="global.http.requestHeaders"></request-headers-vue>
            </div>
        </div>
        <div v-if=" (global.http.requestArgs  && global.http.requestArgs.length>0)">
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
                        v-bind:request-args.sync="global.http.requestArgs"></request-args-vue>
            </div>
        </div>
        <div v-if="(global.http.responseHeaders&&global.http.responseHeaders.length>0)">
            <p class="doc-item-section-title">全局响应头</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-7">描述</li>
                </ul>
                <request-headers-vue
                        v-bind:request-headers.sync="global.http.responseHeaders"></request-headers-vue>
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



        <div v-if="(content.requestHeaders  && content.requestHeaders.length>0)">
            <p class="doc-item-section-title">请求头</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-7">描述</li>
                </ul>
                <request-headers-vue
                        v-bind:request-headers.sync="content.requestHeaders"></request-headers-vue>
            </div>
        </div>

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


        <div v-if="(content.requestHeaders  && content.requestHeaders.length>0)">
            <p class="doc-item-section-title">响应头</p>
            <div class="div-table">
                <ul class="div-table-header div-table-line cb">
                    <li class="col-sm-2">参数名称</li>
                    <li class="col-sm-1">是否必须</li>
                    <li class="col-sm-2">默认值</li>
                    <li class="col-sm-7">描述</li>
                </ul>
                <request-headers-vue
                        v-bind:request-headers.sync="content.responseHeaders"></request-headers-vue>
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
                <pre class="content" v-html="content.example"></pre>
            </div>
        </div>
        <p class="doc-item-section-title">演示</p>
        <div>
            <div class="form">
                <div class="item">
                    <div class="col-sm-2 label second">请求地址</div>
                    <div class="col-sm-8">
                        <input type="text" class="text" id="requestURL" v-model="requestURL">
                    </div>
                    <div class="col-sm-2">
                        <div class="uk-button-group" v-if="global.environment.length>0">
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
                    <div class="item" v-for="x(item,index) in urlArgs">
                        <div class="col-sm-2 label">{{item.name}}</div>
                        <div class="col-sm-8">
                            <input data-type="text" v-model="item.tempValue" type="text"
                                   :name="item.name" placeholder="替换URL上的参数"
                                   class="text">
                        </div>
                        <div class="col-sm-2">
                            <div class="uk-inline">
                                <span uk-icon="icon:triangle-down"></span>
                                <div uk-dropdown>
                                    <ul class="uk-nav uk-dropdown-nav">
                                        <li v-for="a in algorithms" v-on:click="algorithmClick(index,a.fn,urlArgs)" ><a href="#">{{a.name}}</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="formHeaders.length>0">
                <form class="api-test form" id="header-form">
                    <p class="doc-item-section-title second">请求头</p>
                    <div class="item" v-for="x(item,index) in formHeaders">
                        <div class="col-sm-2 label">{{item.name}}</div>
                        <div class="col-sm-8">
                            <input type="text" :name="item.name" v-model="item.tempValue"
                                   :placeholder="item.description" class="text">
                        </div>
                        <div class="col-sm-2">
                            <div class="uk-inline">
                                <span uk-icon="icon: triangle-down"></span>
                                <div uk-dropdown>
                                    <ul class="uk-nav uk-dropdown-nav">
                                        <li v-for="a in algorithms" v-on:click="algorithmClick(index,a.fn,formHeaders)" ><a href="#">{{a.name}}</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>


            <form class="form" id="args-form" v-on:submit.prevent>
                <div v-if="formArgs.length>0">
                    <div class="cb">
                        <div>
                            <div v-if="content.dataType=='XML' || content.dataType =='JSON'">
                                <div class="item">
                                    <div class="col-sm-2 label">Body</div>
                                    <div class="col-sm-8">
                                        <pre id="ace-editor-box"></pre>
                                    </div>
                                </div>
                            </div>
                            <div v-else>
                                <p class="doc-item-section-title second">请求参数</p>
                                <div class="item"  v-for="x(item,index) in formArgs">
                                    <div class="col-sm-2 label">{{item.name}}</div>
                                    <div class="col-sm-8" v-bind:class="{'full-text':item.type=='file'}">
                                        <input :data-type="item.type"
                                               v-if="item.type=='file'"
                                               type="file"
                                               :name="item.name"
                                               class="api-request-args-item"
                                               :placeholder="item.description"
                                               v-bind:class="{'text':item.type!='file'}">
                                        <input :data-type="item.type"
                                               v-else
                                               v-model="item.tempValue"
                                               type="text"
                                               :name="item.name"
                                               class="api-request-args-item"
                                               :placeholder="item.description"
                                               v-bind:class="{'text':item.type!='file'}">

                                    </div>
                                    <div class="col-sm-2">
                                        <div class="uk-inline">
                                            <span uk-icon="icon: triangle-down"></span>
                                            <div uk-dropdown>
                                                <ul class="uk-nav uk-dropdown-nav">
                                                    <li v-for="a in algorithms" v-on:click="algorithmClick(index,a.fn,formArgs)" ><a href="#">{{a.name}}</a></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div v-if="content.dataType=='RAW'">
                    <p class="doc-item-section-title second">请求数据</p>
                    <div class="item">
                        <div class="col-sm-2 label">body</div>
                        <div class="col-sm-8">
                            <textarea class="text" id="rawBody"></textarea>
                        </div>
                    </div>
                </div>
                <div v-if="content.dataType=='BINARY'">
                    <p class="doc-item-section-title second">请求数据</p>
                    <div class="item">
                        <div class="col-sm-2 label">BINARY</div>
                        <div class="col-sm-8">
                            <input type="file" class="full-text" id="binaryBody">
                        </div>
                    </div>
                </div>

                <div class="form">
                    <div class="item">
                        <div class="col-sm-2 label"></div>
                        <div class="col-sm-8">
                            <div v-if="!hasXyjPlugin">
                                <input type="button" data-ignore v-on:click.stop="proxySubmit"
                                       class="btn btn-primary" :value="apiLoading?'加载中':'代理请求'">
                                <input type="button" data-ignore v-on:click.stop="localSubmit"
                                       class="btn btn-primary" :value="apiLoading?'加载中':'直接运行'">
                            </div>
                            <div v-if="hasXyjPlugin">
                            <input type="button" data-ignore v-on:click.stop="pluginSubmit"
                                   class="btn btn-primary" :value="apiLoading?'加载中':'运行'">
                                </div>
                            <%--<input type="button" data-ignore v-on:click.stop="apiMock" v-show="content.responseArgs && content.responseArgs.length>0"
                                   class="btn btn-orange" value="mock">--%>

                        </div>
                    </div>
                </div>
            </form>
            <!--<p class="doc-item-section-title">结果数据</p>-->
            <div class="api-result-tabs cb" v-show="result.content || result.resultHeaders">
                <a class="tab fl active" v-on:click="resultActive='content'" v-bind:class="{'active':(resultActive=='content')}">Body</a>
                <a class="tab fl" v-on:click="resultActive='headers'" v-bind:class="{'active':(resultActive=='headers')}">Headers</a>
                <a class="tab fr">Time: {{result.resultRunTime}} ms</a>
                <a class="tab fr">StatusCode: {{result.resultStatusCode}}</a>
            </div>
            <div v-show="result.content || result.resultHeaders" class="api-result-box">
                <i v-show="!!result.content && (resultActive=='content')" data-clipboard-target="#api-result" class="content-copy iconfont icon-copy"></i>
                <i v-show="!!result.content && content.contentType=='HTML' && (resultActive=='content')" class="iconfont icon-openwindow" v-on:click="openNewWindow"></i>
                <i v-show="!!result.content && (resultActive=='headers')" id="api-result-header-copy" class="iconfont icon-copy"></i>
                <div id="api-result">
                    <pre v-show="resultActive=='content'" id="api-result-content" v-html="result.content"></pre>
                    <div v-show="resultActive=='headers'" id="api-result-headers">
                        <div class="api-result-headers-list" v-show="result.resultHeaders" v-html="result.resultHeaders"></div>
                        <div class="api-result-headers-list">
                            <div>
                                No header for you
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%--<div class="ta-c api-plugin-tip" v-if="!extVer">
                <i class="iconfont icon-chrome"></i><br/>
                <p>由于浏览器有跨域限制，如果您的服务器不支持CORS协议，需要安装我们开发的Chrome插件“小幺鸡”</p>
                <p>安装的时候请注意勾选，安装后请刷新页面。</p>
                <p>
                    <a href="https://chrome.google.com/webstore/detail/%E5%B0%8F%E5%B9%BA%E9%B8%A1/omohfhadnbkakganodaofplinheljnbd" target="_blank" class="btn btn-default">Chrome应用商店</a>
                </p>
            </div>
            <div v-else>
                <div class="ta-c api-plugin-tip" v-if="extVer < 1.3">
                    <i class="iconfont icon-chrome"></i><br/>
                    <p>您安装的『小幺鸡』插件版本有更新,为了避免使用出现bug,请下载升级</p>
                    <p>安装的时候请注意勾选，安装后请刷新页面。</p>
                    <p>
                        <a href="https://chrome.google.com/webstore/detail/%E5%B0%8F%E5%B9%BA%E9%B8%A1/omohfhadnbkakganodaofplinheljnbd" target="_blank" class="btn btn-default">Chrome应用商店</a>
                        <a href="/extension/xiaoyaoji.crx" target="_blank" class="btn btn-default">本地下载</a>
                        <a href="http://jingyan.baidu.com/article/e5c39bf56286ae39d6603374.html" target="_blank" class="btn btn-default">本地下载安装教程</a>
                    </p>
                </div>
            </div>--%>
        </div>
    </div>
</div>
<jsp:include page="../../includes/doc/table/request-args.jsp"/>
<jsp:include page="../../includes/doc/table/request-headers.jsp"/>
<jsp:include page="../../includes/doc/table/response-args.jsp"/>

<script>
    var doc = ${doc},projectGlobal=${projectGlobal};
</script>
<link rel="stylesheet" type="text/css" href="${assets}/jsonformat/jsonFormater.css"/>
<script src="${ctx}/plugin?plugin=/sys.http/view.js"></script>