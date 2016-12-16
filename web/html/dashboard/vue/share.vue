<template>
    <template v-if="!status.loading">
        <!-- module start -->

        <div class="api-modules">
            <div class="cb api-modules-container">
                <ul>
                    <li data-id="{{item.id}}" v-for="item in modules"
                        class="api-module fl api-module-item"
                        v-bind:class="{'active':currentModule.id == item.id}">
                        <span v-on:click="moduleClick(item)">{{item.name}}</span>
                    </li>
                    <li class="fr api-module api-env" v-if="currentEnv" v-on:click.stop="envClick($event)">{{currentEnv.name}} <i class="iconfont icon-angeldownblock"></i></li>
                    <li class="fr api-module">
                        <div class="api-module-search">
                            <input type="text" class="text" v-on:keyup.enter="search" v-model="flag.searchInput">
                            <i class="iconfont icon-search" v-on:click="search"></i>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div class="api-env-details" id="api-env-details" v-show="status.showEnvs" v-on:mouseleave="status.showEnvs=false">
            <ul class="api-env-items">
                <li v-for="item in envs" v-bind:class="{'active':item.t==currentEnv.t}" v-on:click="currentEnv=item" v-on:mouseover="envOver(item,$event)">{{item.name}}</li>
                <li class="line"></li>
            </ul>
            <div class="api-env-content" id="api-env-content" v-show="status.showEnvValues">
                <div class="div-table">
                    <ul class="div-table-header div-table-line cb">
                        <li class="col-sm-4">变量</li>
                        <li class="col-sm-8">
                            <div class="cb">值</div>
                        </li>
                    </ul>
                    <ul class="div-table-line cb" v-for="item in flag.tempEnv.vars">
                        <li class="col-sm-4">{{item.name}}</li>
                        <li class="col-sm-8">{{item.value}}</li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- module end -->

        <div v-show="modules.length>0">
        <!-- view start -->
        <div>
            <template v-if="!error.projectNotExists">
                <div class="apis">
                    <div v-if="currentModule.folders && currentModule.folders.length>0" class="cb api-container">
                        <div class="fl apis-left">
                            <ul class="apis-nav api-folder-list">
                                <li>
                                    <div class="api-name api-description" v-bind:class="{'active':show=='doc'}"
                                         v-on:click="show='doc'">
                                        <span>文档说明</span>
                                        <span class="fr api-actions">
                                        <i class="iconfont icon-list" v-on:click.stop="collapse=!collapse"></i>
                                    </span>
                                    </div>
                                </li>
                                <template v-if="!currentModule.folders">
                                    {{currentModule.folders = []}}
                                </template>
                                <li v-for="item in currentModule.folders" class="cb">
                                    <!--<span class="fr handle">::</span>-->
                                    <div class="api-name api-folder" v-bind:class="{'open':!collapse}" v-on:click="folderClick">
                                        <span>{{item.name}}</span>
                                    </div>
                                    <ul class="apis-nav apis-nav-sub" v-bind:class="{'hide':collapse}">
                                        <li v-for="api in item.children" v-on:click="show='api'">
                                            <div class="api-name" v-bind:class="{'active':currentApi.id == api.id}"
                                                 v-on:click="apiClick(api,item)">
                                                <span v-bind:class="{'deprecated':api.status=='DEPRECATED'}">{{api.name}}</span>
                                            </div>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                        <div class="api-content fl">
                            <span class="api-update-time">更新时间: <span id="api-update-time">{{currentModule.lastUpdateTime}}</span></span>
                            <div id="api-doc-desc" class="api-doc-desc" v-show="show=='doc'">
                                <div id="view-box" v-show="project.details"></div>
                                <div v-show="!project.details" class="ta-c api-error-tip">
                                    <i class="iconfont icon-info"></i>
                                    <p>项目还未书写文档说明。</p>
                                </div>
                            </div>
                            <div id="api-details" class="api-details" v-show="show=='api'">
                                <p class="api-details-title">基本信息</p>
                                <div class="api-base-info api-edit-box">
                                    <p v-if="currentApi.protocol">请求类型: {{currentApi.protocol}}</p>
                                    <p v-if="currentApi.url">接口地址: {{requestURL}}</p>
                                    <template v-if="currentApi.protocol=='HTTP'">
                                        <p v-if="currentApi.requestMethod">请求方式: {{currentApi.requestMethod}}</p>
                                        <p v-if="currentApi.dataType">数据类型: {{currentApi.dataType}}</p>
                                        <p v-if="currentApi.contentType">响应类型: {{currentApi.contentType}}</p>
                                    </template>
                                    <p v-if="currentApi.status">接口状态: {{currentApi.status=='DEPRECATED'?'已废弃':'启用'}}</p>
                                </div>
                                <template v-if="currentApi.description">
                                    <p class="api-details-title">接口描述</p>
                                    <div>{{{currentApi.description}}}</div>
                                </template>
                                <template v-if="(currentModule.requestHeaders&&currentModule.requestHeaders.length>0)">
                                    <p class="api-details-title">全局请求头</p>
                                    <div class="div-table">
                                        <ul class="div-table-header div-table-line cb">
                                            <li class="col-sm-2">参数名称</li>
                                            <li class="col-sm-1">是否必须</li>
                                            <li class="col-sm-2">默认值</li>
                                            <li class="col-sm-7">描述</li>
                                        </ul>
                                        <request-headers-vue
                                                v-bind:request-headers.sync="currentModule.requestHeaders"
                                                v-bind:editing="editing"></request-headers-vue>
                                    </div>
                                </template>

                                <template v-if="(currentApi.requestHeaders  && currentApi.requestHeaders.length>0)">
                                    <p class="api-details-title">请求头</p>
                                    <div class="div-table">
                                        <ul class="div-table-header div-table-line cb">
                                            <li class="col-sm-2">参数名称</li>
                                            <li class="col-sm-1">是否必须</li>
                                            <li class="col-sm-2">默认值</li>
                                            <li class="col-sm-7">描述</li>
                                        </ul>
                                        <request-headers-vue
                                                v-bind:request-headers.sync="currentApi.requestHeaders"
                                                v-bind:editing="editing"></request-headers-vue>
                                    </div>
                                </template>

                                <template v-if="(currentModule.requestArgs && currentModule.requestArgs.length>0)">
                                    <p class="api-details-title">全局请求参数</p>
                                    <div class="div-table">
                                        <ul class="div-table-header div-table-line cb">
                                            <li class="col-sm-2">参数名称</li>
                                            <li class="col-sm-1">是否必须</li>
                                            <li class="col-sm-1">类型</li>
                                            <li class="col-sm-2">默认值</li>
                                            <li class="col-sm-6">描述</li>
                                        </ul>
                                        <request-args-vue
                                                v-bind:request-args.sync="currentModule.requestArgs"
                                                v-bind:editing="editing"></request-args-vue>
                                    </div>
                                </template>

                                <template v-if=" (currentApi.requestArgs  && currentApi.requestArgs.length>0)">
                                    <p class="api-details-title">请求参数</p>
                                    <div class="div-table">
                                        <ul class="div-table-header div-table-line cb">
                                            <li class="col-sm-2">参数名称</li>
                                            <li class="col-sm-1">是否必须</li>
                                            <li class="col-sm-1">类型</li>
                                            <li class="col-sm-2">默认值</li>
                                            <li class="col-sm-6">描述</li>
                                        </ul>
                                        <request-args-vue
                                                v-bind:request-args.sync="currentApi.requestArgs"
                                                v-bind:editing="editing"></request-args-vue>
                                    </div>
                                </template>
                                <template v-if="currentApi.responseArgs && currentApi.responseArgs.length>0">
                                    <p class="api-details-title">响应数据</p>
                                    <div class="div-table">
                                        <ul class="div-table-header div-table-line cb">
                                            <li class="col-sm-3">参数名称</li>
                                            <li class="col-sm-1">是否必须</li>
                                            <li class="col-sm-2">数据类型</li>
                                            <li class="col-sm-6">描述</li>
                                        </ul>
                                        <response-args-vue
                                                v-bind:response-args.sync="currentApi.responseArgs"
                                                v-bind:editing="editing"></response-args-vue>
                                    </div>
                                </template>

                                <template v-if="currentApi.example">
                                    <p class="api-details-title">例子</p>
                                    <div class="api-details-desc api-edit-box">
                                        <pre class="content">{{currentApi.example}}</pre>
                                    </div>
                                </template>
                                <p class="api-details-title">演示</p>
                                <template v-if="!currentApi.protocol || currentApi.protocol == 'HTTP'">
                                    <div class="form">
                                        <div class="item">
                                            <div class="col-sm-2 label second">请求地址</div>
                                            <div class="col-sm-8">
                                                <input type="hidden" value="{{requestURL}}">
                                                <input type="text" class="text" id="requestURL">
                                            </div>
                                            <div class="col-sm-2" style="text-align: right" v-if="currentEnv">
                                                <div class="xyj-dropdown">
                                                    <span class="api-view-env xyj-dropdown-toggle">{{currentEnv.name}}  <i class="iconfont icon-angeldownblock"></i></span>
                                                    <ul class="xyj-dropdown-list api-view-env-items">
                                                        <li v-for="item in envs" v-bind:class="{'active':item.t==currentEnv.t}"
                                                            v-on:click="currentEnv=item"
                                                            v-on:mouseover="envOver(item,$event)">{{item.name}}</li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <template v-if="(currentModule.requestHeaders && currentModule.requestHeaders.length>0) || (currentApi.requestHeaders && currentApi.requestHeaders.length>0)">
                                        <form class="api-test form" id="header-form">
                                            <p class="api-details-title second">请求头</p>
                                            <div class="item" v-for="item in currentModule.requestHeaders">
                                                <div class="col-sm-2 label">{{item.name}}</div>
                                                <div class="col-sm-8">
                                                    <input type="text" name="{{item.name}}"
                                                           value="{{item.testValue || item.defaultValue}}"
                                                           placeholder="{{item.description}}" class="text">
                                                </div>
                                            </div>
                                            <div class="item" v-for="item in currentApi.requestHeaders">
                                                <div class="col-sm-2 label">{{item.name}}</div>
                                                <div class="col-sm-8">
                                                    <input type="text" name="{{item.name}}"
                                                           value="{{item.testValue || item.defaultValue}}"
                                                           placeholder="{{item.description}}" class="text">
                                                </div>
                                            </div>
                                        </form>
                                    </template>

                                    <div class="form">

                                    <template v-if="currentApi.urlArgs && currentApi.urlArgs.length>0">
                                        <p class="api-details-title second">地址参数</p>
                                        <div class="item" v-for="item in currentApi.urlArgs">
                                            <div class="col-sm-2 label">{{item.name}}</div>
                                            <div class="col-sm-8">
                                                <input data-type="text" v-model="item.value" type="text"
                                                       name="{{item}}" placeholder="替换URL上的参数"
                                                       class="text">
                                            </div>
                                        </div>
                                    </template>
                                    </div>
                                    <form class="form" id="args-form" v-on:submit.prevent>
                                        <template v-if="(currentModule.requestArgs && currentModule.requestArgs.length>0) || (currentApi.requestArgs && currentApi.requestArgs.length>0)">
                                            <div class="cb">
                                                <div>
                                                    <template v-if="currentApi.dataType=='XML' || currentApi.dataType =='JSON'">
                                                        <div class="item">
                                                            <div class="col-sm-2 label">Body</div>
                                                            <div class="col-sm-8">
                                                                <pre id="ace-editor-box"></pre>
                                                            </div>
                                                        </div>
                                                    </template>
                                                    <template v-else>
                                                        <p class="api-details-title second">请求参数</p>
                                                        <div class="item"  v-for="item in currentModule.requestArgs">
                                                            <div class="col-sm-2 label">{{item.name}}</div>
                                                            <div class="col-sm-8" v-bind:class="{'full-text':item.type=='file'}">
                                                                <input data-type="{{item.type}}"
                                                                       type="{{item.type=='file'?'file':'text'}}"
                                                                       name="{{item.name}}"
                                                                       class="api-request-args-item"
                                                                       value="{{item.testValue || item.defaultValue}}"
                                                                       placeholder="{{item.description}}"
                                                                       v-bind:class="{'text':item.type!='file'}">
                                                            </div>
                                                        </div>
                                                        <div class="item"  v-for="item in currentApi.requestArgs">
                                                            <div class="col-sm-2 label">{{item.name}}</div>
                                                            <div class="col-sm-8" v-bind:class="{'full-text':item.type=='file'}">
                                                                <input data-type="{{item.type}}"
                                                                       type="{{item.type=='file'?'file':'text'}}"
                                                                       name="{{item.name}}"
                                                                       class="api-request-args-item"
                                                                       value="{{item.testValue || item.defaultValue}}"
                                                                       placeholder="{{item.description}}"
                                                                       v-bind:class="{'text':item.type!='file'}">
                                                            </div>
                                                        </div>
                                                    </template>
                                                </div>
                                            </div>
                                        </template>

                                        <template v-if="currentApi.dataType=='RAW'">
                                            <p class="api-details-title second">请求数据</p>
                                            <div class="item">
                                                <div class="col-sm-2 label">body</div>
                                                <div class="col-sm-8">
                                                    <textarea class="text" id="rawBody"></textarea>
                                                </div>
                                            </div>
                                        </template>
                                        <template v-if="currentApi.dataType=='BINARY'">
                                            <p class="api-details-title second">请求数据</p>
                                            <div class="item">
                                                <div class="col-sm-2 label">BINARY</div>
                                                <div class="col-sm-8">
                                                    <input type="file" class="full-text" id="binaryBody">
                                                </div>
                                            </div>
                                        </template>



                                        <div class="form">
                                            <div class="item">
                                                <div class="col-sm-2 label"></div>
                                                <div class="col-sm-8">
                                                    <input type="button" id="api-submit" v-on:click.stop="apiSubmit"
                                                           class="btn btn-primary" value="{{status.apiLoading?'加载中':'发送'}}">
                                                    <input type="button" v-on:click.stop="apiMock" v-show="currentApi.responseArgs && currentApi.responseArgs.length>0"
                                                           class="btn btn-orange" value="mock">

                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                    <!--<p class="api-details-title">结果数据</p>-->
                                    <div class="api-result-tabs cb" v-show="currentApi.result">
                                        <a class="tab fl active" v-on:click="flag.resultActive='content'" v-bind:class="{'active':(flag.resultActive=='content')}">Body</a>
                                        <a class="tab fl" v-on:click="flag.resultActive='headers'" v-bind:class="{'active':(flag.resultActive=='headers')}">Headers</a>
                                        <a class="tab fr">Time: {{currentApi.resultRunTime}} ms</a>
                                        <a class="tab fr">StatusCode: {{currentApi.resultStatusCode}}</a>
                                    </div>
                                    <div v-show="currentApi.result" class="api-result-box">
                                        <i v-show="!!currentApi.result && (flag.resultActive=='content')" id="api-result-copy" class="iconfont icon-copy"></i>
                                        <i v-show="!!currentApi.result && currentApi.contentType=='HTML' && (flag.resultActive=='content')" class="iconfont icon-openwindow" v-on:click="openNewWindow"></i>
                                        <i v-show="!!currentApi.result && (flag.resultActive=='headers')" id="api-result-header-copy" class="iconfont icon-copy"></i>
                                        <div id="api-result">
                                            <pre v-show="flag.resultActive=='content'" id="api-result-content">{{{currentApi.result}}}</pre>
                                            <div v-show="flag.resultActive=='headers'" id="api-result-headers">
                                                <div class="api-result-headers-list" v-show="currentApi.resultHeaders">
                                                    {{{currentApi.resultHeaders | html}}}
                                                </div>
                                                <div v-else class="api-result-headers-list">
                                                    <div v-if="extVer>=1.4">
                                                        No header for you
                                                    </div>
                                                    <div v-else>
                                                        请下载或升级浏览器插件。
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="ta-c api-plugin-tip" v-if="!extVer">
                                        <i class="iconfont icon-chrome"></i><br/>
                                        <p>由于浏览器有跨域限制，如果您的服务器不支持CORS协议，需要安装我们开发的Chrome插件“小幺鸡”</p>
                                        <p>安装的时候请注意勾选，安装后请刷新页面。</p>
                                        <p>
                                            <a href="https://chrome.google.com/webstore/detail/%E5%B0%8F%E5%B9%BA%E9%B8%A1/omohfhadnbkakganodaofplinheljnbd" target="_blank" class="btn btn-default">Chrome应用商店</a>
                                            <a href="/extension/xiaoyaoji.crx" target="_blank" class="btn btn-default">本地下载</a>
                                            <a href="http://jingyan.baidu.com/article/e5c39bf56286ae39d6603374.html" target="_blank" class="btn btn-default">本地下载安装教程</a>
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
                                    </div>
                                </template>
                                <template v-if="currentApi.protocol == 'WEBSOCKET'">
                                    <div class="form">
                                        <div class="item">
                                            <div class="col-sm-2 label">日志</div>
                                            <div class="col-sm-10"><textarea class="text" v-model="ws.log"
                                                                             rows="10"></textarea></div>
                                        </div>
                                        <div class="item">
                                            <div class="col-sm-2 label">地址</div>
                                            <div class="col-sm-10">
                                                <input type="text" class="text" id="websocketRequestURL"
                                                                          value="{{requestURL}}"
                                                                          v-model="ws.url"></div>
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
                                </template>
                            </div>
                        </div>
                    </div>
                    <div class="ta-c api-error-tip" v-cloak v-else>
                        <i class="iconfont icon-api" style="font-size: 120px"></i>
                        <p style="font-size: 24px">该模块下接口列表为空</p>
                        <p style="font-size: 12px">编辑模式可管理接口</p>
                    </div>
                </div>

            </template>
            <template v-if="error.projectNotExists">
                <div class="ta-c api-error-tip">
                    <i class="iconfont icon-cloud"></i>
                    <p style="font-size: 24px">项目不可见或不存在</p>
                </div>
            </template>
        </div>
        <!-- view end -->
        </div>
        <div v-else>
            <div class="ta-c api-error-tip">
                <i class="iconfont icon-cloud"></i>
                <p style="font-size: 24px">模块列表为空,请先创建模块</p>
            </div>
        </div>

        <!-- modal list start -->
        <div class="modal" v-cloak v-if="flag.needPassword">
            <div class="modal-header">
            </div><br/><br/>
            <div class="modal-content api-share">
                <div class="modal-layout-box form ">
                    <p class="title">请输入密码</p>
                    <div class="item">
                        <div class="col-sm-2 label">阅读密码</div>
                        <div class="col-sm-10">
                            <input type="text" name="password" v-model="flag.password" maxlength="10" class="text" placeholder="请输入阅读密码">
                        </div>
                    </div>
                    <div class="item">
                        <div class="col-sm-2 label"></div>
                        <div class="col-sm-10">
                            <button class="btn btn-primary middle" v-on:click="confirmPassword" tabindex="2">确定</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- modal list end -->
    </template>

    <!-- loading start -->
    <template v-if="status.loading">
        <div class="spinner">
            <div class="double-bounce1"></div>
            <div class="double-bounce2"></div>
        </div>
    </template>
    <!-- loading end -->
</template>


<script>
    import js from '../app/share';
    export default js;
</script>