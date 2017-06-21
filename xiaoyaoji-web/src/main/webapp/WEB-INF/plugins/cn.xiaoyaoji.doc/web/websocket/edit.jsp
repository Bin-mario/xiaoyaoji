<%--
  User: zhoujingjie
  Date: 17/5/6
  Time: 16:19
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${assets}/css/http.css?v=${v}"/>
<div id="doc">
    <div v-cloak>
        <datalist id="requestlist">
            <option v-for="item in flag.requests" :value="item">
        </datalist>
        <datalist id="responselist">
            <option v-for="item in flag.responses" :value="item">
        </datalist>

        <div id="api-edit-details">
            <div id="api-edit-content" class="form">
                <ul uk-tab><li class="uk-active"><a>基本信息</a></li></ul>
                <div class="item">
                    <div class="col-sm-1 label">接口名称</div>
                    <div class="col-sm-11">
                        <input type="text" class="uk-input" maxlength="30" placeholder="请输入接口名称"
                               v-model="doc.name" :value="doc.name">
                    </div>
                </div>
                <div class="item" v-if="global.status.length>0">
                    <div class="col-sm-1 label">状态</div>
                    <div class="col-sm-11">
                        <select v-model="content.status" class="uk-select">
                            <option :value="item.name"  v-for="item in global.status" :selected="item.name==content.status">{{item.name}}</option>
                        </select>
                    </div>
                </div>

                <div class="item">
                    <div class="col-sm-1 label">请求地址</div>
                    <div class="col-sm-11">
                        <input type="text" placeholder="如:/api/test" v-model="content.url" class="uk-input"
                               :value="requestURL">
                        <div v-if="currentEnv.vars && currentEnv.vars.length>0">
                            <p class="hint">实际请求地址:{{requestURL}}</p>
                            <p class="api-env-vars">
                                变量：<span v-on:click="apiVarsClick(item.name,$event)" v-for="item in currentEnv.vars">{{item.name}}</span>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="item">
                    <div class="col-sm-1 label">接口描述</div>
                    <div class="col-sm-11">
                        <div contenteditable="true" id="api-description" class="uk-textarea" rows="6" v-html="content.description">
                        </div>
                    </div>
                </div>
                <div>
                    <ul uk-tab>
                        <li class="uk-active"><a>请求参数(Body)</a></li>
                    </ul>
                    <!-- 请求参数 -->
                    <div class="tab-content" v-cloak  v-show="flag.tab=='body'">
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
                            <request-args-vue :name="'requestArgs'" v-bind:request-args="content.requestArgs"
                                              v-bind:editing="editing"></request-args-vue>
                        </div>
                        <div class="item">
                            <button class="btn btn-default btn-sm" v-on:click="newRow('requestArg')">
                                <i class="iconfont icon-tianjia"></i>添加参数
                            </button>
                            <button class="btn btn-default btn-sm" v-on:click="importJSON('requestArg')">
                                <i class="iconfont icon-importexport"></i>导入json
                            </button>
                        </div>
                    </div>

                    <!-- 响应参数 -->
                    <ul uk-tab>
                        <li class="uk-active"><a>响应数据(Body)</a></li>
                    </ul>
                    <!-- 响应数据 -->
                    <div v-cloak v-show="flag.resp=='body'">
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
                            <response-args-vue :name="'responseArgs'" v-bind:response-args="content.responseArgs"
                                               v-bind:editing="editing"></response-args-vue>
                        </div>
                        <div class="item">
                            <button class="btn btn-default btn-sm" v-on:click="newRow('responseArg')">
                                <i class="iconfont icon-tianjia"></i>添加参数
                            </button>
                            <button class="btn btn-default btn-sm" v-on:click="importJSON('responseArg')">
                                <i class="iconfont icon-importexport"></i>导入json
                            </button>
                        </div>
                    </div>

                    <ul uk-tab>
                        <li class="uk-active"><a>示例数据</a></li>
                    </ul>
                            <textarea class="api-example api-field uk-textarea" v-model="content.example"
                                      placeholder="请添加一些示例数据">{{content.example}}</textarea>
                    <ul uk-tab>
                        <li class="uk-active"><a>附件</a></li>
                    </ul>
                    <div class="doc-http-attach">
                        <input v-on:change="fileUpload" multiple type="file">
                        <p>点击、拖拽可上传文件。单文件不能超过1M</p>
                    </div>
                    <br/>
                    <div class="cb" v-if="attachs && attachs.length>0">
                        <div class="doc-attach" v-for="item in attachs" v-bind:class="{'file':item.type=='FILE'}">
                            <i class="iconfont icon-close" v-on:click="deleteFile(item)"></i>
                            <a :href="fileAccess+item.url" v-if="item.type=='FILE'" target="_blank">{{item.fileName}}</a>
                            <img v-if="item.type =='IMG'" v-bind:src="fileAccess+item.url" :onclick="'window.open('+fileAccess+item.url+');'">
                        </div>
                    </div>
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
<jsp:include page="/WEB-INF/includes/doc/table/request-args.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/response-args.jsp"/>

<script>
    var doc = ${doc},projectGlobal=${projectGlobal};
</script>
<script src="${ctx}/plugin?plugin=/sys.websocket/edit.js"></script>