<%--
  User: zhoujingjie
  Date: 17/4/25
  Time: 21:20
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="global">
    <ul uk-tab>
        <li class="uk-active" v-on:click="uitab='http'"><a href="#">全局参数</a></li>
        <li v-on:click="uitab='environment'"><a href="#">环境变量</a></li>
        <li v-on:click="uitab='http-status'"><a href="#">接口状态</a></li>
        <%--<li class="uk-disabled"><a href="#">Disabled</a></li>--%>
    </ul>
    <div v-cloak v-show="uitab=='environment'">
        <div class="http-environment">
            <div class="cb">
                <div class="col-sm-2">&nbsp;</div>
                <div class="col-sm-3">变量</div>
                <div class="col-sm-4">值</div>
                <div class="col-sm-3">操作</div>
            </div>
            <div v-for="item in global.environment">
                <div class="cb">
                    <div class="col-sm-2">{{item.name}}</div>
                    <div class="col-sm-3">&nbsp;</div>
                    <div class="col-sm-4">&nbsp; </div>
                    <div class="col-sm-3">
                        <i v-on:click.stop="envEdit(item)" style="padding-right: 5px" class="iconfont icon-edit"></i>
                        <i v-on:click.stop="envCopy(item)" style="padding-right: 5px" class="iconfont icon-copy"></i>
                        <i v-on:click.stop="global.environment.$remove(item)" style="padding-right: 5px" class="iconfont icon-close"></i>
                    </div>
                </div>
                <div>
                    <div class="cb" v-for="v in item.vars">
                        <div class="col-sm-2">&nbsp;</div>
                        <div class="col-sm-3">{{v.name}} </div>
                        <div class="col-sm-4">{{v.value}} </div>
                        <div class="col-sm-3"></div>
                    </div>
                </div>
            </div>

            <div>
                <button class="btn btn-primary" v-on:click="createEnv">创建</button>
            </div>

        </div>

        <!-- environment start -->
        <div class="modal env-modal" v-cloak v-if="envModal">
            <div class="modal-header">
                <i class="iconfont icon-close modal-close" v-on:click="envModal=false"></i>
            </div>
            <div class="modal-content">
                <div class="modal-layout1 form" style="width: 500px">
                    <p class="title" style="margin-bottom: 20px">添加新环境</p>
                    <div class="hint">
                        环境变量运行在URL中,你可以配置多个(线上、灰度、开发)环境变量。在URL中使用方式{{flag.varname}},例：<br/>
                        线上环境：prefix => http://www.xiaoyaoji.com.cn<br/>
                        则<br/>
                        请求URL：{{flag.prefix}}/say => http://www.xiaoyaoji.com.cn/say
                    </div>
                    <p class="title"></p>
                    <div class="item">
                        <div class="col-sm-12">
                            <input type="text" class="text" v-model="flag.tempEnv.name" placeholder="请输入环境名称">
                        </div>
                    </div>
                    <div class="item" v-for="(item,index) in flag.tempEnv.vars">
                        <div class="col-sm-5"><input type="text" v-model=item.name class="text" v-on:focus="envNewLine(index)" placeholder="变量名称" :value="item.name"></div>
                        <div class="col-sm-6">
                            <input type="text" class="text" v-model="item.value" placeholder="变量值" :value="item.value">
                        </div>
                        <div class="col-sm-1 full-text">
                            <i class="iconfont icon-close" v-if="flag.tempEnv.vars.length>1" v-on:click="flag.tempEnv.$remove(item)"></i>
                        </div>
                    </div>

                    <div class="ta-c actions">
                        <button class="btn btn-default-box middle" tabindex="3" v-on:click="envModal=false">取消</button>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <button class="btn btn-primary middle" v-on:click="envSave" tabindex="2">确定</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- environment end -->


    </div>

    <div v-cloak v-show="uitab=='http'">
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
                            <li class="col-sm-2">默认值</li>
                            <li class="col-sm-4">描述</li>
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
    </div>
    <div v-cloak v-show="uitab=='http-status'">
        <div>
            <button v-on:click="tempStatus={name:'',value:''}" uk-toggle="target:#modal-status" class="uk-button uk-button-default">新建</button>
        </div>
        <table class="uk-table">
            <thead>
            <tr>
                <th>状态名称</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in global.status">
                <td>{{item.name}}</td>
                <td><i v-on:click.stop="tempStatus=item" uk-toggle="target:#modal-status"  class="iconfont icon-edit"></i>
                    <i v-on:click.stop="global.status.$remove(item)" class="iconfont icon-close"></i></td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="modal" v-cloak v-if="importModal">
        <div class="modal-header">
            <i class="iconfont icon-close modal-close" v-on:click="importModal=false"></i>
        </div>
        <div class="modal-content">
            <div class="modal-layout1 form" style="width: 500px">
                <validator name="if">
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
                </validator>
            </div>
        </div>
    </div>

    <!-- This is the modal -->
    <div id="modal-status" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title">新建状态</h2>
            <div class="uk-modal-body">
                <label>变量名称:</label>
                <input class="uk-input" type="text" :value="tempStatus.name" autofocus="" v-model="tempStatus.name">
            </div>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                <button class="uk-button uk-button-primary uk-modal-close" v-on:click="statusOk" type="button">确定</button>
            </p>
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
<script src="${assets}/js/project/global.js"></script>