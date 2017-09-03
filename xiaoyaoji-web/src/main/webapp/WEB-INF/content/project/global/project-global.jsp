<%--
  User: zhoujingjie
  Date: 17/4/25
  Time: 21:20
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../doc/doc-header.jsp"/>
<style>
    body{
        min-width:inherit;
    }
    .xd-header,.xd-header-placeholder{
        display: none;
    }
</style>
<div id="global">

        <datalist id="headerlist">
            <option v-for="item in flag.headers" :value="item">
        </datalist>
        <datalist id="requestlist">
            <option v-for="item in flag.requests" :value="item">
        </datalist>
        <datalist id="responselist">
            <option v-for="item in flag.responses" :value="item">
        </datalist>


        <div id="api-edit-details">
            <div id="api-edit-content" class="form">
                <!-- 请求头参数 -->
                <p class="doc-item-section-title">全局请求头</p>
                <div class="tab-content">
                    <div class="div-table">
                        <ul class="div-table-header div-table-line cb">
                            <li class="col-sm-1">操作</li>
                            <li class="col-sm-3">参数名称</li>
                            <li class="col-sm-2">是否必须</li>
                            <li class="col-sm-2">默认值</li>
                            <li class="col-sm-4">描述</li>
                        </ul>
                    </div>
                    <div class="div-table editing div-editing-table">
                        <request-headers-vue
                                :name="'requestHeaders'"
                                v-bind:request-headers.sync="global.http.requestHeaders"
                                v-bind:editing="editing"></request-headers-vue>
                    </div>
                    <div class="item">
                        <button class="btn btn-default btn-sm" v-on:click="newRow('requestHeaders')">
                            <i class="iconfont icon-tianjia"></i>添加参数
                        </button>
                        <button class="btn btn-default btn-sm" v-on:click="importJSON('requestHeaders')">
                            <i class="iconfont icon-importexport"></i>导入json
                        </button>
                    </div>
                </div>

                <!-- 请求参数 -->
                <p class="doc-item-section-title">全局请求数据</p>
                <div class="tab-content">
                    <div class="div-table">
                        <ul class="div-table-header div-table-line cb">
                            <li class="col-sm-1">操作</li>
                            <li class="col-sm-3">参数名称</li>
                            <li class="col-sm-2">是否必须</li>
                            <li class="col-sm-2">类型</li>
                            <li class="col-sm-2">默认值</li>
                            <li class="col-sm-2">描述</li>
                        </ul>
                    </div>
                    <div class="div-table editing div-editing-table">
                        <request-args-vue :name="'requestArgs'"
                                          v-bind:request-args.sync="global.http.requestArgs"
                                          v-bind:editing="editing"></request-args-vue>
                    </div>
                    <div class="item">
                        <button class="btn btn-default btn-sm" v-on:click="newRow('requestArgs')">
                            <i class="iconfont icon-tianjia"></i>添加参数
                        </button>
                        <button class="btn btn-default btn-sm" v-on:click="importJSON('requestArgs')">
                            <i class="iconfont icon-importexport"></i>导入json
                        </button>
                    </div>
                </div>

                <!-- 请求头参数 -->
                <p class="doc-item-section-title">全局响应头</p>
                <div class="tab-content">
                    <div class="div-table">
                        <ul class="div-table-header div-table-line cb">
                            <li class="col-sm-1">操作</li>
                            <li class="col-sm-3">参数名称</li>
                            <li class="col-sm-2">是否必须</li>
                            <li class="col-sm-6">描述</li>
                        </ul>
                    </div>
                    <div class="div-table editing div-editing-table">
                        <response-headers-vue :name="'responseHeaders'"
                                v-bind:response-headers.sync="global.http.responseHeaders"
                                v-bind:editing="editing"></response-headers-vue>
                    </div>
                    <div class="item">
                        <button class="btn btn-default btn-sm" v-on:click="newRow('responseHeaders')">
                            <i class="iconfont icon-tianjia"></i>添加参数
                        </button>
                        <button class="btn btn-default btn-sm" v-on:click="importJSON('responseHeaders')">
                            <i class="iconfont icon-importexport"></i>导入json
                        </button>
                    </div>
                </div>

                <!-- 响应参数 -->
                <p class="doc-item-section-title">全局响应数据</p>
                <div>
                    <div class="div-table">
                        <ul class="div-table-header div-table-line cb">
                            <li class="col-sm-1">操作</li>
                            <li class="col-sm-3">参数名称</li>
                            <li class="col-sm-2">是否必须</li>
                            <li class="col-sm-2">类型</li>
                            <li class="col-sm-4">描述</li>
                        </ul>
                    </div>
                    <div class="div-table editing div-editing-table">
                        <response-args-vue v-bind:response-args="global.http.responseArgs" :name="'responseArgs'"
                                           v-bind:editing="editing"></response-args-vue>
                    </div>
                    <div class="item">
                        <button class="btn btn-default btn-sm" v-on:click="newRow('responseArgs')">
                            <i class="iconfont icon-tianjia"></i>添加参数
                        </button>
                        <button class="btn btn-default btn-sm" v-on:click="importJSON('responseArgs')">
                            <i class="iconfont icon-importexport"></i>导入json
                        </button>
                    </div>
                </div>
            </div>
        </div>
    <div class="modal" v-cloak v-if="importModal">
        <div class="modal-header">
            <i class="iconfont icon-close modal-close" v-on:click="importModal=false"></i>
        </div>
        <div class="modal-content">
            <div class="modal-layout1 form" style="width: 500px">
                <p class="title">导入JSON</p>
                <textarea rows="15" class="k1 text" v-model="importValue" initial="off"
                          v-bind:autofocus="importModal"
                          tabindex="1" placeholder="请粘贴导入的数据"></textarea>
                <div class="ta-c actions">
                    <button class="btn btn-default-box middle" tabindex="3"
                            v-on:click="importModal=false">
                        取消
                    </button>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <button class="btn btn-primary middle" v-on:click="importOk" tabindex="2">确定</button>
                </div>
            </div>
        </div>
    </div>
    </div>



<jsp:include page="/WEB-INF/includes/doc/table/request-headers.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/request-args.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/response-headers.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/response-args.jsp"/>
<script>
    var global = ${projectGlobal},_projectId_='${project.id}';
</script>
<script src="${assets}/js/project/global.js?v=${v}"></script>

</div>

</body>
</html>