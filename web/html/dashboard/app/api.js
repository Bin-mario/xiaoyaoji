import RequestHeadersVue from "../vue/api-editor-request-headers.vue";
import RequestArgsVue from "../vue/api-editor-request-args.vue";
import ResponseArgsVue from "../vue/api-editor-response-args.vue";
import utils from "../../src/utils";
import "../../assets/jsonformat/jsonFormater.js";
import "../../assets/jsonformat/jsonFormater.css";
//todo 浏览模式的环境切换样式
//todo 编辑模式仿造postman
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
        folderModal: false, //文件夹新增编辑模态
        moduleModal: false, //模块新增编辑模态
        importModal: false, //导入json模态
        shareModal:false,    //分享模态
        shareCreateModal:false,
        envModal: false,    //环境变量编辑模态
        loading: true,      //loading
        apiLoading: false,  //api价值
        moveCopyModal: false,//复制移动模态
        moveCopyId: '' ,     //复制移动id
        showEnvs:false,      //环境变量显示状态
        showEnvValues:false,
        showModuleGlobal:false,
        showHistory:false
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
        import: null,
        actionId: null,//api下拉
        moduleActionId:'',//module 下拉
        move: null,
        moveCopyName: null,
        moveCopySelectModuleId: null,
        moveCopySelectFolderId: null,
        moveCopyId: null,
        resultActive: 'content',
        tempEnv:{vars:[]}   ,
        prefix:'$prefix$',
        varname:'$变量名$',
        tab:'body',
        headers:["User-Agent","Accept","Accept-Charset","Accept-Encoding","Accept-Language","Accept-Datetime","Authorization","Cache-Control","Connection","Cookie","Content-Length","Content-MD5","Content-Type"],
        requests:["name","id","password","email","createtime","datetime","createTime","dateTime","user","code","status","type","msg","message","time","image","file","token","accesstoken","access_token","province","city","area","description","remark","logo"],
        responses:["name","id","password","email","createtime","datetime","createTime","dateTime","user","code","status","type","msg","message","error","errorMsg","test","fileAccess","image","require","token","accesstoken","accessToken","access_token","province","city","area","remark","description","logo"],
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
    importValue: null,  //导入的json值
    editing: false,     //编辑状态
    folderName: '',     //文件夹名称
    moduleName: '',     //模块名称
    show:'doc',
    modules: [],        //当前项目的所有模块数据
    tempModules:[],     //用于搜索
    project: {},        //当前项目数据
    currentApi: {result: null},//当前api对象
    currentModule: {},  //当前模块对象
    currentFolder: null,  //当前文件夹数据
    currentEnv: null,   //当前环境变量数据
    id: '',             //当前项目id
    extVer: false,      //浏览器扩展版本
    collapse: false,    //文件夹折叠状态
    results: {},         //所有结果临时数据
    shares:null
};
//页面全局变量
var page = {
    formater:{xml:new XML.ObjTree()},
    listener:{
        success:function (e) {
            new Result().resolve(e.detail, gdata.currentApi.contentType);
        },
        error:  function (e) {
            console.error('error:'+e.detail);
        },
        complete:  function (e) {
            xhrComplete(gdata, e);
        }
    },
    pushMessage:function(data){
        var detail = {createTime:new Date().toLocaleString(),content:data};
        document.dispatchEvent(new CustomEvent('project.message',{detail:detail}));
    },
    updateInterface:function(id){
        utils.get('/interface/'+id+'.json',{},function(rs){
            page.pushMessage('修改接口：'+rs.data.interface.name);
            let isBreak;
            gdata.modules.forEach(function(m){
                m.folders.forEach(function(f){
                    let index = -1;
                    let originalFolderId;
                    f.children.forEach(function(item,i){
                        if(item.id == id){
                            index = i;
                            originalFolderId=item.folderId;
                            return true;
                        }
                    });
                    if(rs.data.interface.folderId == originalFolderId){
                        if(index>=0){
                            f.children.$set(index,rs.data.interface);
                            isBreak = true;
                            return true;
                        }
                    }
                });
                if(isBreak){
                    return true;
                }
            });
        });
    },
    createInterface:function(newInterfaceId,folderId){
        utils.get('/interface/'+newInterfaceId+'.json',{},function(rs){
            page.pushMessage('新增接口：'+rs.data.interface.name);
            gdata.modules.forEach(function(m){
                let isBreak;
                m.folders.forEach(function(item,i){
                    if(item.id == folderId){
                        item.children.push(rs.data.interface);
                        isBreak = true;
                        page.sortable.interface();
                        return true;
                    }
                });
                if(isBreak){
                    return true;
                }
            });
        });
    },
    deleteInterface:function(id){
        gdata.modules.forEach(function(m){
            let isBreak;
            m.folders.forEach(function(f){
                var index = null;
                f.children.forEach(function(item,i){
                    if(item.id == id){
                        index = item;
                        isBreak=true;
                        return true;
                    }
                });
                if(index){
                    if(gdata.currentApi.id == index.id){
                        gdata.show = 'doc';
                    }
                    page.pushMessage('删除接口：'+index.name);
                    f.children.$remove(index);
                    return true;
                }
            });
            if(isBreak){
                return true;
            }
        });
    },
    createFolder:function(newfolderId,moduleId){
        utils.get('/interfacefolder/'+newfolderId+'.json',{},function(rs){
            page.pushMessage('新增分类：'+rs.data.folder.name);
            gdata.modules.forEach(function(item){
                if(item.id == moduleId){
                    item.folders.push(rs.data.folder);
                    page.sortable.folder();
                    return true;
                }
            });
        });
    },
    updateFolder:function(id){
        utils.get('/interfacefolder/'+id+'.json',{},function(rs){
            page.pushMessage('更新分类：'+rs.data.folder.name);
            gdata.modules.forEach(function(m){
                let isBreak;
                m.folders.forEach(function(item,index){
                    if(item.id == id){
                        rs.data.folder.children = item.children;
                        let folder = $.extend(item,rs.data.folder);
                        m.folders.$set(index,folder);
                        isBreak= true;
                        return true;
                    }
                });
                if(isBreak){
                    return true;
                }
            });
        });
    },
    deleteFolder:function(folderId){
        gdata.modules.forEach(function(m){
            let folder = null;
            m.folders.forEach(function(item,index){
                if(item.id == folderId){
                    folder = item;
                    return true;
                }
            });
            if(folder!=null){
                if(gdata.currentApi.folderId == folder.id){
                    gdata.show = 'doc';
                }
                m.folders.$remove(folder);
                page.pushMessage('删除分类：'+folder.name);
                return true;
            }
        });
    },
    createModule:function(moduleId){
        utils.get('/module/'+moduleId+'.json',{},function(rs){
            page.pushMessage('新增模块：'+rs.data.module.name);
            gdata.modules.push(rs.data.module);
        });

    },
    updateModule:function(moduleId){
        utils.get('/module/'+moduleId+'.json',{},function(rs){
            page.pushMessage('更新模块：'+rs.data.module.name);
            gdata.modules.forEach(function(item,index){
                if(item.id == moduleId){
                    rs.data.module.folders = item.folders;
                    let module = $.extend(item,rs.data.module);
                    gdata.modules.$set(index,module);
                }
            });
        });
    },
    deleteModule:function(moduleId){
        let module;
        gdata.modules.forEach(function(item){
            if(item.id == moduleId){
                module = item;
                return true;
            }
        });
        if(module){
            if(gdata.currentModule.id == module.id){
                if (gdata.modules.length > 0) {
                    gdata.currentModule = gdata.modules[0];
                    gdata.show = 'doc';
                } else {
                    gdata.error.noModule = true;
                }
            }
            page.pushMessage('删除模块：'+module.name);
            gdata.modules.$remove(module);
        }
    },
    task:{
        instance:null,
        num:0,
        init:function(){
            var ws = new WebSocket(utils.config.websocket+'/api/message');
            this.instance = ws;
            var self = this;
            function reconnect(){
                if(self.num<3){
                    ws = new WebSocket(utils.config.websocket+'/api/message');
                    console.log('reconnect');
                }
            }
            ws.onopen = function (evt) {
                ws.send("projectId:"+gdata.id);
                setInterval(function(){
                    if(ws.readyState == ws.OPEN){
                        ws.send("projectId:"+gdata.id);
                    }
                },55000);
            };
            ws.onclose = function (evt) {
                switch (evt.code){
                    case 1006:
                    case 1001:
                        setTimeout(reconnect,5000);
                        break
                }

            };
            ws.onmessage = function (evt) {
                var data = utils.toJSON(evt.data);
                if(data.projectId != gdata.id){
                    return;
                }
                if(data.token.substring(0,10) == utils.token().substring(0,10)){
                    return ;
                }
                switch (data.action){
                    case "interface.update":
                        page.updateInterface(data.interfaceId);
                        break;
                    case "interface.create":
                    case "interface.copy":
                        page.createInterface(data.interfaceId,data.ext[0]);
                        break;
                    case "interface.delete":
                        page.deleteInterface(data.interfaceId);
                        break;
                    case "folder.create":
                        page.createFolder(data.folderId,data.ext[0]);
                        break;
                    case "folder.update":
                        page.updateFolder(data.folderId);
                        break;
                    case "folder.delete":
                        page.deleteFolder(data.folderId);
                        break;
                    case "module.create":
                        page.createModule(data.moduleId);
                        break;
                    case "module.update":
                        page.updateModule(data.moduleId);
                        break;
                    case "module.delete":
                        page.deleteModule(data.moduleId);
                        break;

                }
            };
            ws.onerror = function (evt) {
                console.log('onerror');
            };
        },
        destroy:function(){
            if(this.instance){
                this.instance.close();
                this.instance = null;
            }
        }
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
    sortable:{
        init:function(){
            this.folder();
            this.interface();
        },
        folder:function(){
            // 排序
            setTimeout(function(){
                sortable('#api-edit-nav','destroy');
                let parent = sortable('#api-edit-nav');
                $(parent).off('sortupdate').on('sortupdate',function(e){
                    let oldindex = e.detail.oldElementIndex;
                    let nowindex = e.detail.elementIndex;
                    let children =e.detail.endparent.children;
                    let idsorts = [];
                    if(oldindex>nowindex){
                        for(let i=nowindex;i<=oldindex;i++){
                            let $dom = $(children[i]);
                            idsorts.push($dom.data('id')+'_'+$dom.index());
                        }
                    }else{
                        for(let i=nowindex;i>=oldindex;i--){
                            let $dom = $(children[i]);
                            idsorts.push($dom.data('id')+'_'+$dom.index());
                        }
                    }
                    gdata.currentModule.folders.move(oldindex,nowindex);
                    utils.post('/interfacefolder/sort.json',{sort:idsorts.toString()});
                });
            },500);
        },
        interface:function(){
            // 排序
            setTimeout(function(){
                sortable('#api-edit-nav .apis-nav-sub','destroy');
                let sub = sortable('#api-edit-nav .apis-nav-sub');
                $(sub).off('sortupdate').on('sortupdate',function(e){
                    let oldindex = e.detail.oldElementIndex;
                    let nowindex = e.detail.elementIndex;
                    let children =e.detail.endparent.children;
                    let idsorts = [];
                    if(oldindex>nowindex){
                        for(let i=nowindex;i<=oldindex;i++){
                            let $dom = $(children[i]);
                            idsorts.push($dom.data('id')+'_'+$dom.index());
                        }
                    }else{
                        for(let i=nowindex;i>=oldindex;i--){
                            let $dom = $(children[i]);
                            idsorts.push($dom.data('id')+'_'+$dom.index());
                        }
                    }
                    e.detail.item.dispatchEvent(new CustomEvent('apisort',{detail:e.detail}))
                    utils.post('/interface/sort.json',{sort:idsorts.toString()});
                });
            },500);
        }
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
        let data = {},url='/project/' +self.id + '.json';
    utils.get(url, data, function (rs) {
        if (rs.code == 0) {
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
            var isNew = (self.$route.query.n == 'y');
            if (isNew && self.project.editable=='YES') {
                self.editing = true;
                self.show = 'api';
                self.currentFolder = gdata.modules[0].folders[0];
                self.currentApi =page.initInterfaceParameters(self.currentFolder.id);
                initInterface(self, self.currentApi);
            }

            //
            page.sortable.init();
        }
        else{
            toastr.error(rs.errorMsg);
        }
    }, function () {
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
            this.$parent.$data.pageName = '接口列表';
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
                let $body=$('body');
                $body.undelegate('.xyj-dropdown-toggle','click').delegate('.xyj-dropdown-toggle','click',function(e){
                    $(this).next().toggle();
                    e.stopPropagation();
                });

                $body.undelegate('.icon-menu','click').delegate('.icon-menu','click',function(e){
                    let right = document.documentElement.clientWidth - 1485;
                    if(right<15){
                        right =15;
                    }
                    if(document.documentElement.clientWidth<1255){
                        right =document.documentElement.clientWidth-1255 + 15;
                    }
                    let top = $(this).offset().top+50;
                    $('#api-menus').css({top:top,right:right}).show();
                    e.stopPropagation();
                });
                $body.undelegate('.icon-history','click').delegate('.icon-history','click',function(e){
                    let top = $(this).offset().top+50;
                    let right = document.documentElement.clientWidth - 1485;
                    if (right>300){
                        right = document.documentElement.clientWidth - 300 - 1485;
                    } else {
                        if (right<15){
                            right =15;
                        }
                        if(document.documentElement.clientWidth<1255){
                            right =document.documentElement.clientWidth-1255 + 15;
                        }
                    }
                    $('#api-history').css({top:top,right:right});
                    self.status.showHistory=true;
                    e.stopPropagation();
                });

                $(window).resize(function(){
                    let right = document.documentElement.clientWidth - 1485;
                    if (right>300){                        
                        $('#api-menus').css({right:right});
                        $('#api-history').css({right:right-300}); //窗口从大变小时有问题
                    } else {
                        if (right<15){
                            right =15;
                        }
                        if(document.documentElement.clientWidth<1255){
                            right =document.documentElement.clientWidth-1255 + 15;
                        }
                        $('#api-menus').css({right:right});
                        $('#api-history').css({right:right});
                    }
                });

                $(document).click(function(){
                    $('.xyj-dropdown-list').hide();
                    $('#api-menus').hide();
                    self.status.showHistory = false;
                    self.flag.actionId = null;
                    self.status.showEnvs=false;
                    self.flag.moduleActionId = null;
                });
                
            })();
            page.task.destroy();
            page.task.init();

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
            var obj = getRequestArgsObject(this.currentApi.requestArgs);
            var g = getRequestArgsObject(this.currentModule.requestArgs);
            obj = $.extend(true,g,obj);
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
        },
        editable:function(){
            return this.project.editable == 'YES';
        }
    },
    watch: {
        "status.folderModal": function (value) {
            if (!value) {
                var self = this;
                /*setTimeout(function () {
                    self.$data.currentFolder = null;
                }, 100)*/
            }

            if (value) {
                $("body").addClass("modal-open");
            } else {
                $("body").removeClass("modal-open");
            }
        },
        "status.moduleModal": function (value) {
            if (!value) {
                var self = this;
                setTimeout(function () {
                    self.$data.moduleId = '';
                    self.$data.moduleName = '';
                }, 100)
            }

            if (value) {
                $("body").addClass("modal-open");
            } else {
                $("body").removeClass("modal-open");
            }
        },
        "status.importModal": function (value) {
            if (value) {
                $("body").addClass("modal-open");
            } else {
                $("body").removeClass("modal-open");
            }
        },
        "status.envModal":function(value){
            if (value) {
                $("body").addClass("modal-open");
            } else {
                $("body").removeClass("modal-open");
            }
        },
        "id": function (value) {

        },
        "status.loading": function (value) {
            if (!value) {
                document.title = (this.project.name || '') + '-' + (this.currentModule.name || '');
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
        "editing": function (value) {
            if (value) {
                if (!window.editor && this.show=='doc') {
                    var desc = this.project.details;
                    initEditor(desc, this);
                }
                _initsort_();
            } else {
                if(this.show=='doc'){
                    page.renderViewBox(this.project.details);
                }else if(this.show == 'api'){
                    initInterface(this,this.currentApi);
                }else if(this.show =='module'){
                    this.show = 'doc';
                    page.renderViewBox(this.project.details);
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
        updateProject: function () {
            this.project.details = window.editor.getMarkdown();
            utils.post('/project/'+this.id+'.json',{details:this.project.details},function(){
                toastr.success('修改成功');
            })
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
        envEdit:function(){
            this.status.envModal=true;
            this.flag.tempEnv.vars.push({});
        },
        envNewLine:function(index){
            if(index == this.flag.tempEnv.vars.length-1){
                this.flag.tempEnv.vars.push({});
            }
        },
        createEnv:function () {
            this.status.envModal=true;
            this.flag.tempEnv={vars:[{}]};
        },
        envSave:function(){
            let self= this;
            if(!this.flag.tempEnv.name){
                toastr.error('请输入环境名称');
                return false;
            }
            if(this.flag.tempEnv.vars){
                this.flag.tempEnv.vars = this.flag.tempEnv.vars.filter(function(item){
                    return item.name!=undefined && item.name != null && item.name != '';
                });
            }
            if(!this.flag.tempEnv.vars){
                this.flag.tempEnv.vars=[];
            }
            if(this.flag.tempEnv.t){
                let t = this.flag.tempEnv.t;
                let index =this.envs.findIndex(function(item){
                    return item.t ==t;
                });
                if(index!=-1){
                    this.envs.$set(index,this.flag.tempEnv);
                }
            }else{
                this.flag.tempEnv.t = Date.now();
                this.envs.push(this.flag.tempEnv);
            }
            this.envs = this.envs.map(function(item){
                return {name:item.name,t:item.t,vars:item.vars}
            });
            utils.post('/project/'+this.id+'.json',{environments:JSON.stringify(this.envs)},function(rs){
               toastr.success('保存成功');
                self.status.envModal = false;
                if(self.envs.length == 1){
                    self.currentEnv = self.envs[0];
                }else{
                    self.envs.forEach(function(item){
                        if(item.t==self.flag.tempEnv.t){
                            self.currentEnv = item;
                        }
                    })
                }
            });
        },
        envRemove:function(){
            let self = this;
            let t= this.flag.tempEnv.t;
            let index = this.envs.findIndex(function(item){
                return item.t == self.flag.tempEnv.t;
            });
            this.envs.$remove(this.envs[index]);
            utils.post('/project/'+this.id+'.json',{environments:JSON.stringify(this.envs)},function(rs){
                toastr.success('移除成功');
                if(self.envs.length == 0){
                    self.currentEnv = {name:'环境切换',vars:[]};
                }else{
                    if(self.currentEnv.t == t){
                        self.currentEnv=self.envs[0];
                    }
                }
            });
        },
        envCopy:function(){
            var vars = $.extend(true,[],this.flag.tempEnv.vars);
            var temp = {t:Date.now(),name:this.flag.tempEnv.name+'copy',vars:vars};
            this.envs.push(temp);
            let self = this;
            utils.post('/project/'+this.id+'.json',{environments:JSON.stringify(this.envs)},function(rs){
                toastr.success('复制成功');
            });
        },
        folderNew: function (event) {
            this.status.folderModal = true;
            this.currentFolder = null;
            this.folderName = '';
            event.stopPropagation();
            focusFolderName();
        },
        folderEdit: function (item, event) {
            this.status.folderModal = true;
            this.currentFolder = item;
            this.folderName = item.name;
            event.stopPropagation();
        },
        folderSave: function () {
            this.$validate(true);
            if (this.$ff.invalid) {
                return false;
            }
            if (!this.folderName) {
                toastr.error('文件夹名称为空');
                return false;
            }
            var name = this.folderName;
            var self = this;
            if (this.$data.currentFolder) {
                var id = self.currentFolder.id;
                utils.post('/interfacefolder/' + this.currentFolder.id + ".json", {name: name}, function (rs) {
                    if (rs.code == 0) {
                        self.currentModule.folders.forEach(function (item) {
                            if (item.id == id) {
                                item.name = name;
                            }
                        });
                    }
                });
            } else {
                utils.post('/interfacefolder.json', {
                    sort:self.currentModule.folders.length,
                    moduleId: self.currentModule.id,
                    projectId: self.currentModule.projectId,
                    name: name
                }, function (rs) {
                    if (rs.code == 0) {
                        self.currentModule.folders.push({
                            name: name, id: rs.data, children: []
                        });
                        page.sortable.folder();
                    }
                });
            }
            gdata.status.folderModal = false;
            //this.currentFolder=null;
            this.folderName = null;
        },
        folderDelete: function (item, event) {
            if (!confirm('是否确认删除?')) {
                return false;
            }
            var self = this;
            utils.delete('/interfacefolder/' + item.id + ".json", function (rs) {
                self.$data.currentModule.folders.$remove(item);
            });
            event.stopPropagation();
        },
        folderNewApi: function (item, event) {
            event.stopPropagation();
            this.flag.actionId = null;
            this.show = 'api';
            if(item == null){
                if(this.currentFolder){
                    item = this.currentFolder;
                }else{
                    item = this.currentModule.folders[0];
                }
            }
            if(!item){
                toastr.error('请先添加一个分类。');
                this.show = 'doc';
                return false;
            }
            //设置默认数据
            this.currentApi =page.initInterfaceParameters(item.id);
            //设置接口默认位置
            this.currentApi.sort = item.children.length;
            this.currentFolder = item;
            if (document.documentElement.scrollTop > 100) {
                document.documentElement.scrollTop = 110;
            }
        },
        folderClick: function (event) {
            var $dom = $(event.currentTarget);
            $dom.toggleClass("open");
            $dom.next().slideToggle();
        },
        apiDescBlur:function(e){
            this.currentApi.description = $('#api-description').html();
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
        apiDelete: function (item, arr, event) {
            event.stopPropagation();
            if (!confirm('是否确认删除?')) {
                return false;
            }
            let self = this;
            utils.delete('/interface/' + item.id + ".json", function (rs) {
                if(item.id == self.currentApi.id){
                    page.defaultView(self);
                }
                arr.$remove(item);
            });
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
                    page.sortable.interface();
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
                window.open(url + '?' + utils.args2Params(args));
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
        apisortupdate:function(folder,e){
            folder.move(e.detail.oldElementIndex,e.detail.elementIndex);
        },
        moduleDelete: function (item) {
            if(this.modules.length<=1){
                toastr.error('至少需要保留一个模块');
                return false;
            }

            if (!confirm('是否确认删除?')) {
                return false;
            }
            this.modules.$remove(item);
            utils.delete('/module/' + item.id + '.json');
            if (this.modules.length > 0) {
                this.currentModule = this.modules[0];
                this.show = 'doc';
            } else {
                this.error.noModule = true;
            }
        },
        moduleEdit: function (item) {
            this.status.moduleModal = true;
            this.moduleName = item.name;
            this.moduleId = item.id;
            focusModuleName();
        },
        moduleNew: function () {
            this.status.moduleModal = true;
            focusModuleName();
            this.moduleName = '';
        },
        moduleClick: function (item) {
            if (!item.folders) {
                item.folders = [];
            }
            this.currentModule = item;
            this.currentApi = {};
            this.currentFolder = null;
            this.show = 'doc';
            page.sortable.init();
        },
        moduleSave: function () {
            this.$validate(true);
            if (this.$mf.invalid) {
                return false;
            }
            if (!this.moduleName) {
                toastr.error('模块名称为空');
                return false;
            }
            var self = this;
            if (this.moduleId) {
                var moduleId = self.moduleId;
                var name = self.moduleName;
                utils.post('/module/' + moduleId + ".json", {name: name}, function (rs) {
                    if (rs.code == 0) {
                        self.modules.forEach(function (item) {
                            if (item.id == moduleId) {
                                item.name = name;
                            }
                        });
                    }
                });
            } else {
                var moduleName = this.moduleName;
                utils.post('/module.json', {projectId: self.id, name: moduleName}, function (rs) {
                    if (rs.code == 0) {
                        let temp ={
                            name: moduleName,
                            projectId: self.id,
                            id: rs.data,
                            folders: [],
                            requestHeaders:[],
                            requestArgs:[]
                        };
                        gdata.modules.push(temp);
                        page.initModules(temp);
                        if(gdata.modules.length == 1){
                            gdata.currentModule =gdata.modules[0];
                        }
                    }
                });
            }
            gdata.status.moduleModal = false;
            this.moduleName = '';
            this.moduleId = '';
        },
        moduleUpdateCommonParams:function(){
            var requestArgs = JSON.stringify(this.currentModule.requestArgs);
            var requestHeaders = JSON.stringify(this.currentModule.requestHeaders);
            utils.post('/module/'+this.currentModule.id+'.json',{requestArgs:requestArgs,requestHeaders:requestHeaders},function(rs){
                toastr.success('操作成功');
            });
        },
        insertNewResponseArgsRow: function () {
            gdata.currentApi.responseArgs.push({require: "true", children: [], type: 'string'});
            _initsort_();
        },
        insertNewRequestHeadersRow: function () {
            gdata.currentApi.requestHeaders.push({require: 'true', children: []});
            _initsort_();
        },
        insertNewRequestArgsRow: function () {
            gdata.currentApi.requestArgs.push({require: "false", children: [], type: 'string'});
            _initsort_();
        },
        insertNewGRequestHeadersRow:function(){
            this.currentModule.requestHeaders.push({children:[],require:'true'});
            _initsort_();
        },
        insertNewGRequestArgsRow:function(){
            this.currentModule.requestArgs.push({type:'string',children:[],require:'true'})
            _initsort_();
        },
        import2GHeaders(){
            this.status.importModal = true;
            this.flag.import = "gHeaders";
        },
        import2GRequestArgs(){
            this.status.importModal = true;
            this.flag.import = "gRequestArgs";
        },
        import2RequestArgs(){
            this.status.importModal = true;
            this.flag.import = "requestArgs";
        },
        import2RequestHeaders(){
            this.status.importModal = true;
            this.flag.import = "requestHeaders";
        },
        import2ResponseArgs(){
            this.status.importModal = true;
            this.flag.import = "responseArgs";
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
            _initsort_();
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
        listItemCopy: function (type, id, move) {
            if (type == 'api') {
                this.flag.moveCopyName = '接口';
            } else if (type == 'folder') {
                this.flag.moveCopyName = '分类';
            }
            this.flag.move = (move == 'move');
            this.status.moveCopyModal = true;
            this.flag.moveCopyId = id;
        },
        copyMoveOk: function () {
            if (this.flag.move) {
                if (this.flag.moveCopyName == '分类') {
                    if (this.flag.moveCopySelectModuleId == this.currentModule.id) {
                        toastr.error('同一模块无须移动');
                        return false;
                    } else {
                        page.copyMove({type: 'folder', action: 'move', moduleId: this.flag.moveCopySelectModuleId}, this);
                    }
                } else {
                    page.copyMove({
                        type: 'api',
                        action: 'move',
                        moduleId: this.flag.moveCopySelectModuleId,
                        folderId: this.flag.moveCopySelectFolderId
                    },this);
                }
            } else {
                //copy
                if (this.flag.moveCopyName == '分类') {
                    page.copyMove({type: 'folder', action: 'copy', moduleId: this.flag.moveCopySelectModuleId},this);
                } else {
                    page.copyMove({
                        type: 'api',
                        action: 'copy',
                        moduleId: this.flag.moveCopySelectModuleId,
                        folderId: this.flag.moveCopySelectFolderId
                    },this);
                }
            }
        },
        openNewWindow:function(){
            let win = window.open('','new');
            win.document.documentElement.innerHTML='';
            win.document.write(utils.unescape(this.currentApi.result));
            win.document.close();
        },
        shareItemClockClick:function(e){
            var $dom=$(e.target).parent().next();
            $dom.show();
            $dom.focus();
        },
        shareItemPasswordBlur:function(item,e){
            let self = e.target;
            $(self).hide();
            let now = $(self).val();
            let originalValue = $(self).data('value') || '';
            if(now == originalValue){
                return true;
            }
            utils.post('/share/'+item.id+'.json',{password:now},function(rs){
                toastr.success('密码已修改');
            })
        },
        shareCreate:function(){
            let self = this;
            let data = $('#share-form').serialize() + '&projectId='+this.id+"&shareAll="+(this.flag.shareAll?'YES':'NO');
            utils.post('/share.json',data,function(rs){
                self.getShares(true);
                self.status.shareCreateModal=false;
                toastr.success('创建成功');
            });
        },
        confirmPassword:function(){
            page.reget(this);
        },
        getShares:function(reget){
            this.status.shareModal=true;
            if(this.shares == null || reget){
                let self = this;
                utils.get('/project/'+this.id+'/shares.json',{},function(rs){
                    self.shares = rs.data.shares;
                });
            }
        },
        deleteShare:function(item){
            if(!confirm('是否确认删除?')){
                return true;
            }
            let self = this;
            utils.delete('/share/'+item.id+'.json',function(){
                self.shares.$remove(item);
            });
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
    $('#api-description').html(item.description || '');
    _initsort_();

}
(function(){
    Array.prototype.move = function (old_index, new_index) {
        if (new_index >= this.length) {
            var k = new_index - this.length;
            while ((k--) + 1) {
                this.push(undefined);
            }
        }
        this.splice(new_index, 0, this.splice(old_index, 1)[0]);
        return this; // for testing purposes
    };

    //初始化排序
    window._initsort_=function(){
        setTimeout(function(){
            sortable('.div-editing-table','destroy');
            let doms= sortable('.div-editing-table',{
                handle:'.icon-drag-copy',
                items:'.div-editing-line'
            });
            $(doms).off('sortupdate').on('sortupdate',function(e){
                let clazz = $(e.detail.item).attr('class');
                if(clazz.indexOf('placeholder-request-args')!=-1){
                    e.detail.item.dispatchEvent(new CustomEvent('requestargsortupdate',{detail:e.detail}));
                }else if(clazz.indexOf('placeholder-request-headers')!=-1){
                    e.detail.item.dispatchEvent(new CustomEvent('requestheadersortupdate',{detail:e.detail}));
                }else if(clazz.indexOf('placeholder-response-args')!=-1){
                    e.detail.item.dispatchEvent(new CustomEvent('responseargssortupdate',{detail:e.detail}));
                }
            });
        },500);
    };
})();
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
                obj[name] = [getRequestArgsObject(d.children)];
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

function focusFolderName() {
    setTimeout(function () {
        $("#folderName").focus();
    }, 100)
}
function focusModuleName() {
    setTimeout(function () {
        $("#moduleName").focus();
    }, 100)
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
            var t = {children: []};
            t.name = key;
            if (v != undefined) {
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
            } else {
                t.type = '';
            }
            t.require = 'true';
            temp.push(t);
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







