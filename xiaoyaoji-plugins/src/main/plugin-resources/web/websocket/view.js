requirejs(['utils','vue'],function(utils,Vue){

    Vue.filter('html', function (value) {
        if (value) {
            return value.replace(/\n/g, '<br/>')
        }
        return '';
    });
    Vue.filter('text', function (value) {
        if (value) {
            return value.replace(/\</g, '&lt;').replace(/\>/g,'&gt;')
        }
        return '';
    });
    function apiSubmit(proxy){
        var self = this;
        //var url = this.requestURL;
        var url = $('#requestURL').val();
        // 请求参数
        var args = getRequestArgs();
        for (var name in args) {
            var key = self.doc.id + ':args:' + name;
            var value = args[name];
            if (typeof value === 'string') {
                localStorage.setItem(key, value);
            }
        }
        //如果是图片或二进制
        if (this.content.contentType === "IMAGE" || this.content.contentType === 'BINARY') {
            window.open(url + '?' + utils.args2Params(args));
            params = undefined;
            return true;
        }
        //请求头
        var headers = getRequestHeaders();
        if(proxy){
            headers['url']=url;
            url = ctx+'/plugin/http/proxy';
        }
        for (var name in headers) {
            var key = self.doc.id + ':headers:' + name;
            var value = headers[name];
            if (typeof value === 'string') {
                localStorage.setItem(key, value);
            }
        }


        var params = {
            url: url,
            cache: false,
            headers: headers,
            type: this.content.requestMethod,
            data: args,
            beforeSend: function (xhr) {
                xhr.beginTime = Date.now();
            },
            dataType: this.content.contentType,
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
            jsonpCallback: this.content.contentType == 'JSONP' ? 'callback' : undefined,
            complete:function(xhr, status){
                self.apiLoading = false;
                var useTime= Date.now() - xhr.beginTime,body='';
                if(status === "success"){
                    var resp = xhr.responseText;
                    body =  new Result().resolve(resp,self.content.contentType);
                }else{
                    console.error(xhr.statusText);
                }

                self.result.resultHeaders = xhr.getAllResponseHeaders() || '';
                self.result.resultStatusCode = xhr.status || 0;
                self.result.resultRunTime = useTime;
                self.result.content = body;


                if (status !== 'success') {
                    var msg = (xhr.responseText || xhr.statusText);
                    if (status === 'error') {
                        msg = ('status:' + xhr.status + ' readyState:' + xhr.readyState + '  errorText:' + msg);
                    }
                    var error = utils.escape(msg);
                    if (status === 'parsererror') {
                        self.result.content = new Result().resolve(error, self.content.contentType);
                        return true;
                    }
                    if (!xhr.status || xhr.status=== 0) {
                        if(xhr.statusText === 'error'){
                            error ='请求地址错误,服务器无响应或JavaScript跨域错误,详情错误请查看控制台';
                        }
                    }
                    self.result.content = '<div class="db-api-error">' + error + '</div>';
                }


            }
        };

        switch (this.content.dataType) {
            case "FORM-DATA":
                params.contentType = false;
                params.processData = false;
                var data =params.data;
                var fd = new FormData();
                for(var key in data){
                    var value = data[key];
                    if(value && value.constructor.name==='Array'){
                        value.forEach(function (item) {
                            fd.append(key,item);
                        });
                    }else{
                        fd.append(key,value);
                    }
                }
                params.data=fd;
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
                var fileE=$('#binaryBody')[0];
                if(fileE){
                    var reader = new FileReader();
                    reader.onload = function() {
                        var arrayBuffer = this.result,
                            array = new Uint8Array(arrayBuffer)
                            //binaryString = String.fromCharCode.apply(null, array);
                        params.data = array;
                        $.ajax(params);
                    };
                    reader.readAsArrayBuffer(fileE.files[0]);
                    return true;
                }
                break;
            default:
                var data =params.data;
                for(var key in data){
                    var value = data[key];
                    if(value && value.constructor.name==='Array'){
                        var temp = '';
                        value.forEach(function (item) {
                            temp += 'item,';
                        });
                        temp= temp.substr(0,temp.length-1);
                        data[key]=temp;
                    }
                }
                params.data=data;
                break;
        }
        this.apiLoading = true;
        // chrome 插件中jsonp 会出问题
        $.ajax(params);
    }

    Array.prototype.mergeArray = function(source){
        var target = this;
        if(source && source.length>0){
            var targetKey={};
            target.forEach(function(item){
                targetKey[item.name]=item;
            });
            source.forEach(function(item){
                var temp = targetKey[item.name];
                if(!temp){
                    temp = item;
                    targetKey[item.name]=temp;
                    target.push(temp);
                }
                if(!temp.children){
                    temp.children=[];
                }
                if(item.children && item.children.length>0){
                    item.children = temp.children.mergeArray(item.children);
                }

            });
        }
        return target;
    };

    new Vue({
        el:'#docApp',
        data:{
            editing:false,
            doc:doc,
            fileAccess:null,
            attachs:null,
            content:null,
            global:null,
            apiLoading:false,
            ws:{
                instance: null,
                connected: false,
                message: '',
                log: '',
                url: '',
            },
            currentEnv:null,
            urlArgs:[],
            result:{
                content:'',
                resultHeaders:'',
                resultRunTime:'',
                resultStatusCode:''
            }
        },
        mounted:function(){

        },
        created:function(){
            this.content = utils.toJSON(doc.content);
            if(!this.content){
                this.content={};
            }
            if(!this.content.url){
                this.content.url='';
            }
            this.loadAttach();
            var g = projectGlobal;
            if(!g.environment){
                g.environment = [];
            }else{
                g.environment = utils.toJSON(g.environment);
            }
            if(!g.http){
                g.http={};
            }else{
                g.http = utils.toJSON(g.http);
            }

            if(!g.http.requestHeaders){
                g.http.requestHeaders = [];
            }else{
                g.http.requestHeaders = utils.toJSON(g.http.requestHeaders);
            }
            if(!g.http.responseHeaders){
                g.http.responseHeaders = [];
            }else{
                g.http.responseHeaders = utils.toJSON(g.http.responseHeaders);
            }
            if(!g.http.requestArgs){
                g.http.requestArgs = [];
            }else{
                g.http.requestArgs = utils.toJSON(g.http.requestArgs);
            }
            if(!g.http.responseArgs){
                g.http.responseArgs = [];
            }else{
                g.http.responseArgs = utils.toJSON(g.http.responseArgs);
            }

            this.global = g;

            var temp = localStorage.getItem(_projectId_+"_currentEnv");
            if(temp){
                this.currentEnv = JSON.parse(temp);
            }else{
                this.currentEnv = g.environment[0] || {};
            }
            var urlArgs=[];
            var match = this.content.url.match(/(\{[a-zA-Z0-9_]+\})/g);
            if (match !== null && match.length > 0) {
                urlArgs = match;
                urlArgs = urlArgs.map(function (d) {
                    return {name: d.substring(1, d.length - 1), value: null};
                });
            }
            this.urlArgs= urlArgs;
        },
        computed: {
            requestURL: function () {
                var temp = this.content.url;
                if (!temp) {
                    temp = ''
                }else{
                    if (this.currentEnv && this.currentEnv.vars) {
                        this.currentEnv.vars.forEach(function (item) {
                            var reg = new RegExp('\\$' + item.name + '\\$', 'g');
                            temp = temp.replace(reg, item.value);
                        });
                        if (this.urlArgs && this.urlArgs.length > 0) {
                            this.urlArgs.forEach(function (item) {
                                var name = '{' + item.name + '}';
                                var reg = new RegExp(name, 'g');
                                temp = temp.replace(reg, item.value || name)
                            });
                        }
                    }
                }
                //bug
                setTimeout(function(){
                    $('#requestURL').val(temp);
                },300);

                return temp;
            },
            formArgs:function(){
                var args = this.global.http.requestArgs.mergeArray(this.content.requestArgs);
                for(var key in args){
                    var temp = self.doc.id + ':args:' + args[key].name;
                    args[key].testValue = localStorage.getItem(temp);
                }
                return args;
            }
        },
        methods:{
            loadAttach:function(){
                var self = this;
                utils.get('/attach/'+this.doc.id,{projectId:_projectId_},function (rs) {
                    self.attachs = rs.data.attachs || [];
                    self.fileAccess = rs.data.fileAccess || '';
                });
            },
            proxySubmit:function(){
                apiSubmit.call(this,true);
            },
            localSubmit:function(){
                apiSubmit.call(this);
            },
            wsDestroy:function () {
                if (this.ws.instance) {
                    this.ws.instance.close();
                    this.ws.connected = false;
                    this.ws.log = '';
                    this.ws.url = '';
                    this.ws.message = '';
                }
            },
            wsConnect:function(){
                //var url = this.ws.url;
                var url =$('#requestURL').val()
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
            wsDisconnect:function(){
                this.ws.instance.close()
            },
            wsSendMessage:function(){
                this.ws.instance.send(this.ws.message);
                this.ws.log += '\n sent message:' + this.ws.message;
                this.ws.message='';
            },
            openNewWindow:function(){
                var win = window.open('', 'new');
                win.document.documentElement.innerHTML = '';
                win.document.write(utils.unescape(this.result.content));
                win.document.close();
            },
            changeEnv:function(item){
                this.currentEnv=item;
                localStorage.setItem(_projectId_+"_currentEnv",JSON.stringify(item))
            }
        }
    });

});