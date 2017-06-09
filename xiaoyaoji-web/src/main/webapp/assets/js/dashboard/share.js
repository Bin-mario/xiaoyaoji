import RequestHeadersVue from "../vue/api-editor-request-headers.vue";
import RequestArgsVue from "../vue/api-editor-request-args.vue";
import ResponseArgsVue from "../vue/api-editor-response-args.vue";
import utils from "../../src/utils";
import "../../assets/jsonformat/jsonFormater.js";
import "../../assets/jsonformat/jsonFormater.css";
RequestHeadersVue.name = 'request-headers-vue';
RequestHeadersVue.props = ['requestHeaders', 'editing'];

RequestArgsVue.name = 'request-args-vue';
RequestArgsVue.props = ['requestArgs', 'editing'];

ResponseArgsVue.name = 'response-args-vue';
ResponseArgsVue.props = ['responseArgs', 'editing'];
//
//this is nothing
var gdata = {
    status: {
        envModal: false,    //环境变量编辑模态
        loading: true,      //loading
        apiLoading: false,  //api价值
        moveCopyModal: false,//复制移动模态
        moveCopyId: '' ,     //复制移动id
        showEnvs:false,      //环境变量显示状态
        showEnvValues:false,
        showModuleGlobal:false
    },
    ws: { //websocket操作
        instance: null,
        connected: false,
        message: '',
        log: '',
        url: '',
        destroy(){
            if (this.instance) {
                this.instance.close();
                this.connected = false;
                this.log = '';
                this.url = '';
                this.message = '';
            }
        }
    },
    flag: { //临时变量标识对象
        resultActive: 'content',
        tempEnv:{vars:[]}   ,
        prefix:'$prefix$',
        varname:'$变量名$',
        tab:'body',
        searchInput:'',
        shareAll:false,
        needPassword:false,
        password:''
    },
    error:{ //错误标识
        projectNotExists: false,
        noModule: false,
        noInterface: false
    },
    share:true,
    envs:null, //环境变量
   
    folderName: '',     //文件夹名称
    moduleName: '',     //模块名称
    show:'doc',
    modules: [],        //当前项目的所有模块数据
    tempModules:[],     //用于搜索
    project: {},        //当前项目数据
    currentApi: {result: null},//当前api对象
    currentModule: {},  //当前模块对象
    currentFolder: null,  //当前文件夹数据
    currentEnv:null,   //当前环境变量数据
    id: '',             //当前项目id
    extVer: false,      //浏览器扩展版本
    collapse: false,    //文件夹折叠状态
    results: {}         //所有结果临时数据
};
//页面全局变量
var page = {
    formater:{xml:new XML.ObjTree()},
    listener:{
        success:function (e) {
            new Result().resolve(e.detail, gdata.currentApi.contentType);
        },
        error:  function (e) {
            console.log("result.error");
        },
        complete:  function (e) {
            xhrComplete(gdata, e);
        }
    },
    pushMessage:function(data){
        var detail = {createTime:new Date().toLocaleString(),content:data};
        document.dispatchEvent(new CustomEvent('project.message',{detail:detail}));
    },
    defaultView:function(self){
        self.show = 'doc';
    },
    initModules:function(module){
        if(!module.requestArgs){
            module.requestArgs = [];
        }
        if(!module.requestHeaders){
            module.requestHeaders = [];
        }
        if(!module.folders){
            module.folders=[];
        }
        module.requestArgs = utils.toJSON(module.requestArgs);
        module.requestHeaders = utils.toJSON(module.requestHeaders);
    },
    reget:function(self) {
        if (!self) {
            self = gdata;
        }
        self.status.loading = true;
        self.error.projectNotExists = false;
        self.error.noModule = false;
        self.error.noInterface = false;
        self.currentApi = {result: null};
        if(!self.flag.password){
            self.flag.password =localStorage.getItem(self.id+'.pwd');;
        }else{
            localStorage.setItem(self.id+'.pwd',self.flag.password);
        }
        let data = {code:self.flag.password},url='/share/' +self.id + '/view.json';
        utils.get(url, data, function (rs) {
            if (rs.code == 0) {
                if(rs.data.needPassword){
                    self.flag.needPassword = true;
                    document.title='请输入密码';
                    return true;
                }else{
                    self.flag.needPassword = false;
                }
                if (!rs.data.project) {
                    self.error.projectNotExists = true;
                    return;
                }

                var modules = rs.data.modules;
                modules.forEach(function(d){
                    page.initModules(d);
                });
                self.project = rs.data.project;
                if(self.project.environments){
                    self.envs = utils.toJSON(self.project.environments);
                    if(!self.envs){
                        self.envs=[];
                    }
                    if(!Array.isArray(self.envs)){
                        self.envs=[];
                    }
                    var envId = localStorage.getItem(self.id+'_env');
                    if(envId){
                        let temp;
                        self.envs.forEach(function(item){
                            if(item.id == envId){
                                temp = item;
                                return true;
                            }
                        });
                        if(!temp){
                            temp = self.envs[0];
                        }

                        self.currentEnv = temp;
                    }else{
                        self.currentEnv = self.envs[0];
                    }
                    if(!self.currentEnv){
                        self.currentEnv = {name:'环境切换',vars:[]};
                    }
                    if(!self.currentEnv.vars){
                        self.currentEnv.vars =[];
                    }
                }else{
                    self.envs=[];
                    self.currentEnv = {name:'环境切换',vars:[]};
                }
                self.project = Object.assign({}, self.project, self.project);
                gdata.modules = rs.data.modules;
                gdata.tempModules = rs.data.modules;
                gdata.currentModule = gdata.modules[0];
                document.title = (gdata.project.name || '') + '-' + (gdata.currentModule.name || '');
            }
            else{
                toastr.error(rs.errorMsg);
            }
        }, function (rs) {
            if(rs.responseJSON && rs.responseJSON.code == -3){
                self.flag.needPassword = true;
            }
            self.status.loading = false;
        });
},
    copyMove:function(data, self) {
        data.targetId = gdata.flag.moveCopyId;
        gdata.status.moveCopyModal = false;
        data.projectId = gdata.id;
        utils.post('/project/' + gdata.id + '/copymove.json', data, function () {
            toastr.success('操作成功', '', {timeOut: 2000, "positionClass": "toast-top-right"});
            page.reget(self);
        });
    },
    renderViewBox:function(value) {
        $('#view-box').html('');
        setTimeout(function(){
            editormd.markdownToHTML('view-box', {
                htmlDecode: "style,script,iframe",  // you can filter tags decode
                markdown: (value || ''),
                emoji: false,
                taskList: false,
                tex: false,  // 默认不解析
                flowChart: false,  // 默认不解析
                sequenceDiagram: false  // 默认不解析
            });
        },10)
    },
    initInterfaceParameters:function(folderId){
        return {
            protocol: localStorage.getItem('form.protocol') || 'HTTP',
                requestMethod: localStorage.getItem('form.requestMethod') || 'GET',
            dataType: localStorage.getItem('form.dataType') || 'X-WWW-FORM-URLENCODED',
            contentType: localStorage.getItem('form.contentType') ||'JSON',
            requestHeaders: [],
            requestArgs: [],
            responseArgs: [],
            result: '',
            url:'',
            folderId:folderId,
            status:'ENABLE'
        }
    }  
};

//导出数据
export default{
    components: {
        RequestHeadersVue: RequestHeadersVue,
        RequestArgsVue: RequestArgsVue,
        ResponseArgsVue: ResponseArgsVue
    },
    created: function () {
        $("body").removeClass('loading');
    },
    route: {
        data: function (transition) {
            //初始化
            this.currentFolder=null;
            this.currentModule = {};
            this.currentApi={result:null};
            this.id = transition.to.params.id;
            var self = this;
            //如果实时会出现获取不到的问题
            setTimeout(function () {
                self.extVer = document.body.getAttribute("data-ext-version");
                if (self.extVer) {
                    self.extVer = parseFloat(self.extVer);
                }
                console.log('extVer:' + self.extVer);
            }, 1000);
            //
            (function () {
                var clipboard = new Clipboard('#api-result-copy', {
                    target: function () {
                        return document.querySelector('#api-result-content');
                    }
                });
                var headerClipboard = new Clipboard('#api-result-header-copy', {
                    target: function () {
                        return document.querySelector('#api-result-headers');
                    }
                });

                clipboard.on('success', function (e) {
                    //console.log(e);
                });
                clipboard.on('error', function (e) {
                    console.log(e);
                });
            })();

            //
            (function(){
                $('body').undelegate('.xyj-dropdown-toggle','click').delegate('.xyj-dropdown-toggle','click',function(e){
                    $(this).next().toggle();
                    e.stopPropagation();
                });

                $(document).click(function(){
                    $('.xyj-dropdown-list').hide();
                    self.flag.actionId = null;
                    self.status.showEnvs=false;
                });
            })();

            //加载数据
            this.$parent.projectId = this.id;
            this.show= 'doc';
            page.reget(this);

            if (window._hmt) {
                _hmt.push(['_trackPageview', '/project/'+this.id]);
            }
        },
        activate: function (transition) {
            this.$parent.showProject = true;
            $('.dashboard').addClass('max');
            transition.next();
            document.addEventListener('result.success', page.listener.success);
            document.addEventListener('result.complete',page.listener.complete);
            document.addEventListener('result.error',page.listener.error);
        },
        deactivate: function () {
            this.$parent.showProject = false;
            $('.dashboard').removeClass('max');
            window.editor = null;
            document.removeEventListener('result.success',page.listener.success,false);
            document.removeEventListener('result.error',page.listener.error,false);
            document.removeEventListener('result.complete',page.listener.complete);
            page.task.destroy();
        }
    },
    computed: {
        requestURL: function () {
            let temp = this.currentApi.url;
            if(!temp){temp = ''}else{
                if(this.currentEnv && this.currentEnv.vars){
                    this.currentEnv.vars.forEach(function(item){
                        let reg=new RegExp('\\$'+item.name+'\\$','g');
                         temp = temp.replace(reg,item.value);
                    });
                    if(this.currentApi.urlArgs && this.currentApi.urlArgs.length>0){
                        this.currentApi.urlArgs.forEach(function(item){
                            let name = '{'+item.name+'}';
                            let reg = new RegExp(name,'g');
                            temp = temp.replace(reg,item.value || name)
                        });
                    }
                }else{
                    temp = '';
                }
            }
            $('#requestURL').val(temp);
            return temp;
        },
        requestArgsPreview:function(){
            var type = this.currentApi.dataType;
            if(type == 'XML'){
                obj = {xml:obj};
                return formatXml(page.formater.xml.writeXML(obj));
            }else if(type =='JSON'){
                if(obj){
                    return JSON.stringify(obj,null,'\t');
                }
                return '{}';
            }
            return 'data not support';
        }
    },
    watch: {
        "id": function (value) {

        },
        "status.loading": function (value) {
            if (!value) {

                if (window.editor && window.editor) {
                    window.editor.editor.remove();
                    window.editor = null;
                }
                var value = this.project.details;
                if (this.editing && this.show=='doc' && !window.editor) {
                    initEditor(this.project.details, this);
                }
                if (!this.editing) {
                    page.renderViewBox(value);
                }
            }
        },
        "show": function (value) {
            if (value=='doc') {
                this.currentApi = {result:null};
                this.ws.destroy();
                if( this.editing){
                    if (!window.editor) {
                        var desc = this.project.details;
                        initEditor(desc, this);
                    }
                }
            }else if(value =='module'){
                this.currentApi = {result:null};
            }else{
                
            }
        },
        "currentApi.result": function (value) {
            if (this.currentApi.id) {
                if (!this.results[this.currentApi.id]) {
                    this.results[this.currentApi.id] = {};
                }
                this.results[this.currentApi.id].content = value;
            }
        },
        "currentApi.resultHeaders": function (value) {
            if (this.currentApi.id) {
                if (!this.results[this.currentApi.id]) {
                    this.results[this.currentApi.id] = {};
                }
                this.results[this.currentApi.id].headers = value;
            }
        },
        "currentEnv": function (value) {
            if(value){
                localStorage.setItem(this.id+'_env', value.id);
            }
        }
    },
    data: function () {
        return gdata;
    },
    methods: {
        search:function(){
            let modules = [];
            let text= this.flag.searchInput;
            if(text.trim().length == 0){
                modules = gdata.tempModules;
            }else{
                gdata.tempModules.forEach(function(d){
                    let folders=[];
                    d.folders&&d.folders.forEach(function(folder){
                        let isMatch,children=[];
                        folder.children && folder.children.forEach(function(api){
                            if((api.name && api.name.indexOf(text)!=-1)
                                || (api.url && api.url.indexOf(text)!=-1)
                                || (api.description && api.description.indexOf(text)!=-1)
                            ){
                                isMatch = true;
                                children.push(api);
                            }
                        });

                        //如果文件夹名称匹配，则放入所有API
                        if(folder.name.indexOf(text)!=-1){
                            isMatch = true;
                            children = folder.children;
                        }
                        //如果结果匹配,则复制所有数据，不包含children；
                        if(isMatch){
                            let temp = $.extend(true,{},folder);
                            temp.children = children;
                            folders.push(temp);
                        }
                    });
                    //表示已匹配
                    if(folders.length>0){
                        let temp = $.extend(true,{},d);
                        temp.folders = folders;
                        modules.push(temp);
                    }
                });
            }
            gdata.modules = modules;
            if(modules.length>0){
                gdata.currentModule = modules[0];
                if(this.show == 'api'){
                    gdata.currentFolder = modules[0].folders[0];
                    gdata.currentApi = modules[0].folders[0].children[0];
                    initInterface(this,gdata.currentApi);
                }
            }else{
                gdata.currentModule={};
                gdata.currentApi={result:null};
                gdata.currentFolder=[];
            }
            if(!$('#view-box').html()){
                page.renderViewBox(this.project.details);
            }
            //gdata.show = 'doc';
        },
        envOver:function(data,e){
            this.status.showEnvValues=true;
            this.flag.tempEnv = $.extend(true,{},data);
            var top= $(e.target).offset().top - $(e.target).parent().offset().top;
            $('#api-env-content').css('top',top).show();
        },
        envClick:function(e){
            $('#api-env-details').css('left',$(e.currentTarget).offset().left);
            this.status.showEnvs=true;
        },
        folderClick: function (event) {
            var $dom = $(event.currentTarget);
            $dom.toggleClass("open");
            $dom.next().slideToggle();
        },
        apiClick: function (item, folder) {

            this.currentFolder = folder;
            this.show = 'api';
            this.currentApi = item;
            this.currentModule.lastUpdateTime = item.lastUpdateTime;
            initInterface(this, item);

            if (document.documentElement.scrollTop > 100) {
                document.documentElement.scrollTop = 110;
            }

        },
        apiSave: function () {
            let data = this.currentApi;//$.extend({},this.currentApi);
            if (!data.id) {
                data.moduleId = this.currentModule.id;
                data.projectId = this.currentModule.projectId;
            }
            if(!data.folderId ){
                toastr.error('请选择一个分类');
                return;
            }
            let temp = $.extend({}, data);
            temp.urlArgs = undefined;
            temp.requestArgs = JSON.stringify(temp.requestArgs);
            temp.responseArgs = JSON.stringify(temp.responseArgs);
            temp.requestHeaders = JSON.stringify(temp.requestHeaders);
            let self = this;
            let tempFolder = self.currentFolder;
            let tempModule = self.currentModule;
            utils.post('/interface/save.json', temp, function (rs) {
                toastr.success('保存成功', '', {timeOut: 2000, "positionClass": "toast-top-right"});
                let folder;
                tempModule.folders.forEach(function(item){
                    if(item.id == data.folderId){
                        folder = item;
                        return true;
                    }
                });
                //放置到folder中
                if (data.id) {
                    var index = -1;
                    tempFolder.children.forEach(function (item, i) {
                        if (item.id == data.id) {
                            index = i;
                            return true;
                        }
                    });
                    if(tempFolder.id == data.folderId){
                        if (index != -1) {
                            folder.children.$set(index, data);
                        }
                    }else{
                        folder.children.push(data);
                        tempFolder.children.$remove(tempFolder.children[index]);
                        if(tempFolder.id == self.currentFolder.id){
                            self.currentFolder = folder;
                        }
                    }
                } else {
                    data.id = rs.data;
                    folder.children.push(data);
                }
                //设置默认值
                localStorage.setItem('form.protocol',data.protocol);
                localStorage.setItem('form.requestMethod',data.requestMethod);
                localStorage.setItem('form.dataType',data.dataType);
                localStorage.setItem('form.contentType',data.contentType);
            });
        },
        apiVarsClick:function(name,e){
            
            this.currentApi.url = this.currentApi.url+('$'+name+'$')
        },
        apiSubmit: function () {
            var self = this;
            //var url = this.requestURL;
            var url = $('#requestURL').val();
            var args = getRequestArgs();
            for (let name in args) {
                let key = self.currentApi.id + ':args:' + name;
                let value = args[name];
                if (typeof value == 'string') {
                    localStorage.setItem(key, value);
                }
            }
            //如果是图片或二进制
            if (self.currentApi.contentType == "IMAGE" || self.currentApi.contentType == 'BINARY') {
                var params = '';
                for (var p in args) {
                    params += (p + '=' + args[p] + '&');
                }
                window.open(url + '?' + params);
                params = undefined;
                return true;
            }

            var headers = getRequestHeaders();
            for (let name in headers) {
                let key = self.currentApi.id + ':headers:' + name;
                let value = headers[name];
                if (typeof value == 'string') {
                    localStorage.setItem(key, value);
                }
            }


            var params = {
                url: url,
                cache: false,
                headers: headers,
                type: self.currentApi.requestMethod,
                data: args,
                beforeSend: function (xhr) {
                    xhr.beginTime = Date.now();
                },
                dataType: self.currentApi.contentType,
                crossDomain:true,
                xhrFields: {
                    withCredentials: true
                },
                jsonpCallback: self.currentApi.contentType == 'JSONP' ? 'callback' : undefined,
                complete(xhr, status){
                    var e = {
                        type: status,
                        text: (xhr.responseText || xhr.statusText),
                        headers: xhr.getAllResponseHeaders(),
                        readyState: xhr.readyState,
                        responseText: xhr.responseText,
                        status: xhr.status || 0,
                        statusText: xhr.statusText,
                        useTime: Date.now() - xhr.beginTime
                    };
                    if (status != 'success') {
                        var msg = (xhr.responseText || xhr.statusText);
                        if (status == 'error') {
                            msg = ('status:' + xhr.status + ' readyState:' + xhr.readyState + '  errorText:' + msg);
                        }
                        e.text = msg
                    }
                    xhrComplete(self, {detail: e});
                },
                success(rs){
                    new Result().resolve(rs, self.currentApi.contentType);
                    //self.result = rs;
                }
            };
            if(this.currentApi.requestMethod =='DELETE'){
                if(params.url.indexOf('?')!=-1){
                    params.url += '&'+utils.args2Params(args);
                }else{
                    params.url += '?'+utils.args2Params(args);
                }
                params.data = null;
                delete params.data;
            }
            switch (this.currentApi.dataType) {
                case "FORM-DATA":
                    params.contentType = false;
                    params.processData = false;
                    break;
                case "RAW":
                    params.data = $('#rawBody').val() || '';
                    params.processData = false;
                    params.contentType = 'text/plain';
                    break;
                case "XML":
                    params.data = window.aceeditor.getValue();
                    params.processData = false;
                    params.contentType = 'text/xml';
                    break;
                case "JSON":
                    params.data = window.aceeditor.getValue();
                    params.processData = false;
                    params.contentType = 'application/json';
                    break;
                case "BINARY":
                    params.processData = false;
                    params.contentType = 'application/octet-stream';
                    params.data = $('#binaryBody')[0];
                    break;
                default:
                    break;
            }
            self.status.apiLoading = true;
            // chrome 插件中jsonp 会出问题
            if (this.extVer && self.currentApi.contentType != 'JSONP') {
                delete params['complete'];
                delete params['success'];
                delete params['error'];
                delete params['beforeSend'];
                if (this.currentApi.dataType == 'BINARY') {
                    params.data = '#binaryBody';
                }
                var ce = new CustomEvent('request', {
                    detail: params
                });
                document.dispatchEvent(ce);
            } else {
                $.ajax(params);
            }
        },
        apiMock: function () {

            var self = this;
            //如果是图片或二进制
            if(self.currentApi.contentType == 'IMAGE'){
                window.open('http://www.xiaoyaoji.com.cn/test/image/test.jpg');
                return true;
            }else if(self.currentApi.contentType == 'BINARY'){
                window.open('http://www.xiaoyaoji.com.cn/test/binary/blank.exe');
                return true;
            }
            console.log(self.currentApi.responseArgs);
            let rs;
            switch (self.currentApi.contentType){
                case "JSON":
                case "JSONP":
                    rs = mockJSON(self.currentApi.responseArgs);
                    break;
                case "TEXT":
                    rs = "欢迎使用小幺鸡接口文档管理工具。";
                    break;
                case "HTML":
                    rs = "<html><head><title>标题</title></head><body>欢迎使用小幺鸡接口文档管理工具。</body></html>";
                    break;
                case "XML":
                    rs =mockJSON(self.currentApi.responseArgs);
                    rs =formatXml(page.formater.xml.writeXML(rs)) ;
                    break;
            }
            new Result().resolve(rs, self.currentApi.contentType);
        },
        moduleClick: function (item) {
            if (!item.folders) {
                item.folders = [];
            }
            this.currentModule = item;
            this.currentApi = {};
            this.currentFolder = null;
            this.show = 'doc';
        },
        importOk(){
            if (!this.importValue) {
                toastr.error('导入内容为空');
                return false;
            }

            var data = null;
            try {
                data = utils.toJSON(this.importValue)
            } catch (e) {
                alert('JSON格式有误');
                return;
            }
            var temp = [];
            parseImportData(data, temp);
            var self = this;
            temp.forEach(function (d) {
                if (self.flag.import == 'requestArgs') {
                    self.currentApi.requestArgs.push(d);
                } else if (self.flag.import == 'requestHeaders') {
                    self.currentApi.requestHeaders.push(d);
                } else if (self.flag.import == 'responseArgs') {
                    self.currentApi.responseArgs.push(d);
                } else if(self.flag.import == 'gHeaders'){
                    self.currentModule.requestHeaders.push(d);
                } else if(self.flag.import == 'gRequestArgs'){
                    self.currentModule.requestArgs.push(d);
                }
            });
            this.status.importModal = false;
        },
        wsConnect(){
            //var url = this.ws.url;
            var url =$('#websocketRequestURL').val()
            var ws = new WebSocket(url);
            this.ws.instance = ws;
            var self = this;
            ws.onopen = function (evt) {
                self.ws.log = 'connected';
                self.ws.connected = true;
            };
            ws.onclose = function (evt) {
                self.ws.log += '\nonClose!';
                self.ws.connected = false;
            };
            ws.onmessage = function (evt) {
                self.ws.log += '\nonMessage:' + evt.data;
            };
            ws.onerror = function (evt) {
                self.ws.log += '\nonError:' + (evt.data || '');
            };
        },
        wsDisconnect(){
            this.ws.instance.close()
        },
        wsSendMessage(){
            this.ws.instance.send(this.ws.message);
            this.ws.log += '\n sent message:' + this.ws.message;
        },
        openNewWindow:function(){
            let win = window.open('','new');
            win.document.documentElement.innerHTML='';
            win.document.write(utils.unescape(this.currentApi.result));
            win.document.close();
        },
        shareCreate:function(){
            let self = this;
            let data = $('#share-form').serialize() + '&projectId='+this.id+"&shareAll="+(this.flag.shareAll?'YES':'NO');
            utils.post('/share.json',data,function(rs){
                self.shares.push(rs.data);
                self.status.shareCreateModal=false;
                toastr.success('创建成功');
            });
        },
        confirmPassword:function(){
            page.reget(this);
        }
    }
}

//初始化接口
function initInterface(self, item) {
    if (!item.requestArgs) {
        item.requestArgs = []
    }
    if (!item.requestHeaders) {
        item.requestHeaders = []
    }
    if (!item.responseArgs) {
        item.responseArgs = []
    }
    if (!item.resultHeaders) {
        item.resultHeaders = "";
    }
    if (item.requestArgs.constructor.name == 'String') {
        item.requestArgs = JSON.parse(self.currentApi.requestArgs);
    }
    if (item.responseArgs.constructor.name == 'String') {
        item.responseArgs = JSON.parse(self.currentApi.responseArgs);
    }
    if (item.requestHeaders.constructor.name == 'String') {
        item.requestHeaders = JSON.parse(self.currentApi.requestHeaders);
    }
    if (!item.results) {
        item.results = {};
    }
    if (self.results[item.id]) {
        item.result = self.results[item.id].content;
        item.resultHeaders = self.results[item.id].headers;
    } else {
        //必须需要
        item.result = undefined;
        item.resultHeaders = undefined;
        item.resultStatusCode = undefined;
        item.resultRunTime = undefined;
    }
    if (!item.protocol) {
        item.protocol = 'HTTP';
    }
    if (!item.requestMethod) {
        item.requestMethod = 'GET';
    }
    if (!item.dataType) {
        item.dataType = 'X-WWW-FORM-URLENCODED';
    }
    if (!item.contentType) {
        item.contentType = 'JSON';
    }
    if (!item.url) {
        item.url = "";
    }

    initDefaultData(item.requestHeaders);
    initDefaultData(item.requestArgs);
    initDefaultData(item.responseArgs);
    //从地址上获取
    item.urlArgs = [];
    var match = (self.requestURL).match(/(\{[a-zA-Z0-9_]+\})/g);
    if (match != null && match.length > 0) {
        item.urlArgs = match;
        item.urlArgs = item.urlArgs.map(function (d) {
            return {name:d.substring(1, d.length-1),value:null};
        });
    }
    item.requestArgs.forEach(function (d) {
        let key = item.id + ':args:' + d.name;
        let value = localStorage.getItem(key);
        if (value != undefined && value != '') {
            d.testValue = value;
        }
    });
    item.requestHeaders.forEach(function (d) {
        let key = item.id + ':headers:' + d.name;
        let value = localStorage.getItem(key);
        if (value != undefined && value != '') {
            d.testValue = value;
        }
    });

    if (!item.resultHeaders) {
        item.resultHeaders = '';
    }
    if (!item.resultStatusCode) {
        item.resultStatusCode = 0;
    }
    if (!item.resultRunTime) {
        item.resultRunTime = 0;
    }

    self.currentApi = Object.assign({}, item, item);

    //如果是json或者xml的请求数据类型，则提供可视化修改
    if((item.dataType == 'JSON' || item.dataType == 'XML') && !self.editing){
        initAceEditor(item.dataType,self);
    }
}
//初始化ace编辑器
function initAceEditor(type,self){
    var mode;
    if(type == 'JSON'){
        mode = 'ace/mode/json';
    }else if(type =='XML'){
        mode = 'ace/mode/xml';
    }
    setTimeout(function(){
        try {
            let aceeditor=ace.edit("ace-editor-box");
            window.aceeditor = aceeditor;
        }catch(e) {
            window.aceeditor ={
                getValue:function () {return '';},
                setTheme:function () {},
                session:{setMode:function(){}},
                setValue:function(){}
            };
        }
        aceeditor.setTheme("ace/theme/chrome");
        aceeditor.session.setMode(mode);
        aceeditor.setValue(self.requestArgsPreview);
    },300);
}
/**
 * 请求参数转为对象
 * @param data
 * @return object
 */
function getRequestArgsObject(data){
    let obj={};
    data.forEach(function(d){
        let name = d.name;
        switch(d.type){
            case 'string':
                obj[name] = d.testValue || d.defaultValue || '';
                break;
            case 'number':
                obj[name] = d.testValue || d.defaultValue || 0;
                break;
            case 'boolean':
                obj[name] = d.testValue || d.defaultValue || true;
                break;
            case 'object':
                obj[name] = getRequestArgsObject(d.children);
                break;
            case 'array':
                obj[name] = [];
                break;
            case 'array[number]':
                obj[name] = [0,1];
                break;
            case 'array[boolean]':
                obj[name] = [true];
                break;
            case 'array[string]':
                obj[name] = [''];
                break;
            case 'array[object]':
                obj[name] = [getRequestArgsObject({},d.children)];
                break;
            case 'array[array]':
                obj[name] = [[]];
                break;
            default:
                obj[name] = '';
                break;
        }
    });
    return obj;
}

/**
 * 初始化默认值，不初始化vuejs无法监听改变
 * @param arr
 */
function initDefaultData(arr) {
    arr.forEach(function (d) {
        d.children = d.children || [];
        d.testValue = d.testValue || '';
        initDefaultData(d.children)
    });
}

/**
 * 获取数组类型
 * @param value
 * @returns {string}
 */
function getArrayValueType(value) {
    var type = 'array';
    if (value.length > 0) {
        var name = value[0].constructor.name;
        if (name == 'Array') {
            type = 'array[array]';
        } else if (name == 'Object') {
            type = 'array[object]';
        } else if (name == 'String') {
            type = 'array[string]'
        } else if (name == 'Number') {
            type = 'array[number]'
        } else if (name == 'Boolean') {
            type = 'array[boolean]'
        }
    }
    return type;
}
/**
 * 解析导入数据
 * @param data
 * @param temp
 */
function parseImportData(data, temp) {
    if (data.constructor.name == 'Array') {
        var fullObj = {};
        data.forEach(function (d) {
            if (d.constructor.name == 'Object') {
                for (var key in d) {
                    fullObj[key] = d[key];
                }
            } else if (d.constructor.name == 'Array') {
                parseImportData(d, temp);
            }
        });
        parseImportData(fullObj, temp);
    } else if (data.constructor.name == 'Object') {
        for (var key in data) {
            var v = data[key];
            if (v != undefined) {
                var t = {children: []};
                t.name = key;
                if (v.constructor.name == 'Object') {
                    t.type = 'object';
                    parseImportData(v, t.children);
                } else if (v.constructor.name == 'Array') {
                    t.type = getArrayValueType(v);
                    if (t.type == 'array[object]') {
                        parseImportData(v, t.children);
                    } else if (t.type == 'array[array]') {
                        parseImportData(v[0], t.children);
                    }
                } else if (v.constructor.name == 'String') {
                    t.type = 'string'
                } else if (v.constructor.name == 'Number') {
                    t.type = 'number'
                } else if (v.constructor.name == 'Boolean') {
                    t.type = 'boolean'
                }
                t.require = 'true';
                temp.push(t);
            }
        }
    }
}
//
function mockJSON(data){
    let rs={};
    if(!data){
        return [];
    }
     data.forEach(function(item){
         switch (item.type){
             case 'string':
                 rs[item.name]='mock';
                 break;
             case 'number':
                 rs[item.name]=parseInt(Math.random() * 100);
                 break;
             case 'boolean':
                 rs[item.name]=true;
                 break;
             case 'object':
                 if(item.children && item.children.length>0){
                     rs[item.name] = mockJSON(item.children);
                 }else{
                     rs[item.name]={};
                 }
                 break;
             case 'array':
                 rs[item.name]=[];
                 break;
             case 'array[number]':
                 rs[item.name]=[1,2,3,4,5];
                 break;
             case 'array[string]':
                 rs[item.name]=['1','2','3','4','5'];
                 break;
             case 'array[boolean]':
                 rs[item.name]=[true,false];
                 break;
             case 'array[object]':
                 if(item.children && item.children.length>0){
                     rs[item.name] = [mockJSON(item.children)];
                 }else{
                     rs[item.name]=[{}];
                 }
                 break;
             case 'array[array]':
                 rs[item.name]=[[1,2,3],[4,5,6]];
                 break;
         }
     });
    return rs;
}
//
function xhrComplete(self, e) {

    self.status.apiLoading = false;
    if (!e.detail) {
        e.detail = {};
    }
    self.currentApi.resultHeaders = e.detail.headers || '';
    self.currentApi.resultStatusCode = e.detail.status || 0;
    self.currentApi.resultRunTime = e.detail.useTime || 0;
    if (e.detail.type != 'success') {
        var error = e.detail.text;
        if (e.detail.type == 'parsererror') {
            //self.currentApi.result = '<div class="db-api-error">'+error+'</div>';
            new Result().resolve(error, self.currentApi.contentType);
            return true;
        }

        if (e.detail.status == 0) {
            if (self.extVer) {
                error = 'URL请求失败';
            } else {
                error = '请求地址错误,服务器无响应或JavaScript跨域错误';
            }
        }
        self.currentApi.result = '<div class="db-api-error">' + error + '</div>';
    }
}

function Result() {
    var jf = new JsonFormater({
        dom: '#api-result',
        imgCollapsed: '../assets/jsonformat/images/Collapsed.gif',
        imgExpanded: '../assets/jsonformat/images/Expanded.gif'
    });
    var fn = {
        JSON(data){
            try {
                gdata.currentApi.result = jf.doFormat(data);
            } catch (e) {
                gdata.currentApi.result = utils.escape(data);
            }
        },
        JSONP(data){
            gdata.currentApi.result = jf.doFormat(data);
        },
        TEXT(data){
            gdata.currentApi.result = data;
        },
        XML(data){
            if(!window.XMLDocument){
                toastr.error('该浏览器不支持XMLDocument');
                return;
            }
            if (data instanceof XMLDocument) {
                data = new XMLSerializer().serializeToString(data)
            }
            gdata.currentApi.result = utils.escape(data);
        },
        HTML(data){
            gdata.currentApi.result =utils.escape(data) ;
        }
    };

    return {
        resolve: function (data, type) {
            fn[type](data);
        }
    }
}

function getRequestArgs() {
    var args = {};
    $("#args-form input").each(function () {
        var type = this.type;
        var name = this.name;
        if (args[name]) {
            var temp = args[name];
            if (temp.constructor.name != 'Array') {
                args[name] = [];
                args[name].push(temp);
            }
            if (type == 'file') {
                args[name].push(this.files[0] || null)
            } else {
                args[name].push(this.value);
            }
        } else {
            if (type == 'file') {
                args[name] = this.files[0] || null;
            } else {
                args[name] = this.value;
            }
        }
    });
    return args;
}
function getRequestHeaders() {
    var headers = {};
    $("#header-form input").each(function () {
        headers[$(this).attr("name")] = $(this).val();
    });
    headers['Power-By'] = 'http://www.xiaoyaoji.com.cn';
    return headers;
}

function initEditor(value) {
    if(!value){
        value = "";
    }
    var width = 904;
    if (document.documentElement.clientWidth >= 1800) {
        width = 1160;
    }
    window.editor = editormd("editorBox", {
        width: width,
        height: 740,
        path: '../assets/editor.md/lib/',
        theme: "default",
        previewTheme: "default",
        editorTheme: "mdn-like",
        markdown: value,
        codeFold: true,
        //syncScrolling : false,
        toolbarIcons: function () {
            return [
                "undo", "redo", "|",
                "bold", "del", "italic", "quote", "|",
                "h1", "h2", "h3", "h4", "h5", "h6", "|",
                "list-ul", "list-ol", "hr", "|",
                "link", "reference-link", "image", "code", "preformatted-text", "code-block", "table", "datetime", "watch", "preview", "fullscreen", "clear", "search", "|",
                "help", "info"
            ]
        },
        saveHTMLToTextarea: true,    // 保存 HTML 到 Textarea
        searchReplace: true,
        //watch : false,                // 关闭实时预览
        htmlDecode: "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启
        //toolbar  : false,             //关闭工具栏
        //previewCodeHighlight : false, // 关闭预览 HTML 的代码块高亮，默认开启
        emoji: false,
        taskList: false,
        tocm: true,         // Using [TOCM]
        tex: false,                   // 开启科学公式TeX语言支持，默认关闭
        flowChart: false,             // 开启流程图支持，默认关闭
        toolbarAutoFixed: false,
        sequenceDiagram: false,       // 开启时序/序列图支持，默认关闭,
        dialogLockScreen: true,   // 设置弹出层对话框不锁屏，全局通用，默认为true
        dialogShowMask: false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
        dialogDraggable: false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
        //dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
        //dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
        //暂时关闭图片上传
        imageUpload: false,
        imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
        imageUploadURL: "./php/upload.php",
        onload: function () {
            var self = this;
            /*setTimeout(function () {
                self.gotoLine(1);
            }, 100);*/
        }
    });
}







