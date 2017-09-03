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

    <div v-cloak>
        <div class="http-environment">
            <div class="cb">
                <div class="col-sm-2">&nbsp;</div>
                <div class="col-sm-3">变量</div>
                <div class="col-sm-4">值</div>
                <div class="col-sm-3">操作</div>
            </div>
            <div v-for="(item,index) in global.environment">
                <div class="cb">
                    <div class="col-sm-2">{{item.name}}</div>
                    <div class="col-sm-3">&nbsp;</div>
                    <div class="col-sm-4">&nbsp; </div>
                    <div class="col-sm-3">
                        <i v-on:click.stop="envEdit(item)" style="padding-right: 5px" class="iconfont icon-edit"></i>
                        <i v-on:click.stop="copyEnvironment(item)" style="padding-right: 5px" class="iconfont icon-copy"></i>
                        <i v-on:click.stop="removeEnvironment(index)" style="padding-right: 5px" class="iconfont icon-close"></i>
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
                        请求URL：{{flag.prefix}}/hello => http://www.xiaoyaoji.cn/hello
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
                            <i class="iconfont icon-close" v-if="flag.tempEnv.vars.length>1" v-on:click="flag.tempEnv.vars.splice(index)"></i>
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

</div>
<jsp:include page="/WEB-INF/includes/doc/table/request-headers.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/request-args.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/response-headers.jsp"/>
<jsp:include page="/WEB-INF/includes/doc/table/response-args.jsp"/>
<script>
    var global = ${projectGlobal},_projectId_='${project.id}';
</script>
<script src="${assets}/js/project/global.js"></script>
</div>

</body>
</html>