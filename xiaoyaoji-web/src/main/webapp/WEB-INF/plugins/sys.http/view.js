requirejs(['utils', 'vue', ctx + '/assets/jsonformat/jsonFormater.js',
    ctx + '/assets/clipboard/clipboard.min.js',
    ctx + '/plugin?plugin=/sys.http/lib/chrome-extension.js',
    ctx + '/assets/ace/src-min/ace.js',
    ctx + '/assets/xml2json/2json.js',
    ctx + '/assets/xml2json/2xml.js'
], function (utils, Vue, x, Clipboard, Plugin) {
    var xml = new XML.ObjTree();
    //请求头
    function getRequestHeaders() {
        var headers = {};
        var arr = $("#header-form").serializeArray();
        for (var i = 0; i < arr.length; i++) {
            var o = arr[i];
            headers[o.name] = o.value;
        }
        return headers;
    }

    /*  Vue.filter('html', function (value) {
     if (value) {
     return value.replace(/\n/g, '<br/>')
     }
     return '';
     });*/
    Vue.filter('text', function (value) {
        if (value) {
            return value.replace(/\</g, '&lt;').replace(/\>/g, '&gt;')
        }
        return '';
    });

    //请求参数
    function getRequestArgs() {
        var args = {};
        $("#args-form input").each(function () {
            if (!(this.hasAttribute('data-ignore'))) {
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
            }
        });
        return args;
    }


    //结果处理
    function Result() {
        var jf = new JsonFormater({
            dom: '#api-result',
            imgCollapsed: '../assets/jsonformat/images/Collapsed.gif',
            imgExpanded: '../assets/jsonformat/images/Expanded.gif'
        });
        var fn = {
            JSON: function (data) {
                try {
                    return jf.doFormat(data);
                } catch (e) {
                    return utils.escape(data);
                }
            },
            JSONP: function (data) {
                return jf.doFormat(data);
            },
            TEXT: function (data) {
                return data;
            },
            XML: function (data) {
                if (!window.XMLDocument) {
                    toastr.error('该浏览器不支持XMLDocument');
                    return;
                }
                if (data instanceof XMLDocument) {
                    data = new XMLSerializer().serializeToString(data)
                }
                return utils.escape(data);
            },
            HTML: function (data) {
                return utils.escape(data);
            }
        };

        return {
            resolve: function (data, type) {
                return fn[type](data);
            }
        }
    }

    function apiSubmit(runType) {
        var self = this;
        //var url = this.requestURL;
        var url = $('#requestURL').val();
        // 请求参数
        var args = getRequestArgs();
        for (var name in args) {
            var key = self.doc.id + ':args:' + name;
            var value = args[name];
            if (typeof value == 'string') {
                localStorage.setItem(key, value);
            }
        }
        //如果是图片或二进制
        if (this.content.contentType == "IMAGE" || this.content.contentType == 'BINARY') {
            window.open(url + '?' + utils.args2Params(args));
            params = undefined;
            return true;
        }
        //请求头
        var headers = getRequestHeaders();
        if (runType == 'proxy') {
            headers['url'] = url;
            url = ctx + '/plugin/http/proxy';
        }
        for (var name in headers) {
            var key = self.doc.id + ':headers:' + name;
            var value = headers[name];
            if (typeof value == 'string') {
                localStorage.setItem(key, value);
            }
        }


        var params = {
            url: url,
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
            complete: function (xhr, status) {
                self.apiLoading = false;
                var useTime = Date.now() - xhr.beginTime, body = '';
                if (status === "success" || status == 'OK') {
                    var resp = xhr.responseText;
                    body = new Result().resolve(resp, self.content.contentType);
                } else {
                    console.error(xhr.statusText);
                }

                self.result.resultHeaders = xhr.getAllResponseHeaders() || '';
                self.result.resultStatusCode = xhr.status || 0;
                self.result.resultRunTime = useTime;
                self.result.content = body;


                if (status != 'success' && status != 'OK') {
                    var msg = (xhr.responseText || xhr.statusText);
                    if (status == 'error') {
                        msg = ('status:' + xhr.status + ' readyState:' + xhr.readyState + '  errorText:' + msg);
                    }
                    var error = utils.escape(msg);
                    if (status == 'parsererror') {
                        self.result.content = new Result().resolve(error, self.content.contentType);
                        return true;
                    }
                    if (!xhr.status || xhr.status == 0) {
                        if (xhr.statusText == 'error') {
                            error = '请求地址错误,服务器无响应或JavaScript跨域错误,详情错误请查看控制台';
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
                var data = params.data;
                var fd = new FormData();
                for (var key in data) {
                    var value = data[key];
                    if (value && value.constructor.name === 'Array') {
                        value.forEach(function (item) {
                            fd.append(key, item);
                        });
                    } else {
                        fd.append(key, value);
                    }
                }
                params.data = fd;
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
                if (runType == 'plugin') {
                    params.data = '#binaryBody';
                } else {
                    var fileE = $('#binaryBody')[0];
                    if (fileE) {
                        var reader = new FileReader();
                        reader.onload = function () {
                            var arrayBuffer = this.result,
                                array = new Uint8Array(arrayBuffer)
                            //binaryString = String.fromCharCode.apply(null, array);
                            params.data = array;
                            $.ajax(params);
                        };
                        reader.readAsArrayBuffer(fileE.files[0]);
                        return true;
                    }
                }
                break;
            default:
                var data = params.data;
                for (var key in data) {
                    var value = data[key];
                    if (value && value.constructor.name === 'Array') {
                        var temp = '';
                        value.forEach(function (item) {
                            temp += 'item,';
                        });
                        temp = temp.substr(0, temp.length - 1);
                        data[key] = temp;
                    }
                }
                params.data = data;
                break;
        }
        this.apiLoading = true;
        // chrome 插件中jsonp 会出问题

        if (runType == 'plugin' && this.content.contentType != 'JSONP') {
            Plugin.complete = params['complete'];
            Plugin.success = params['success'];
            Plugin.error = params['error'];
            delete params['complete'];
            delete params['success'];
            delete params['error'];
            delete params['beforeSend'];
            Plugin.run();
            var ce = new CustomEvent('request', {
                detail: params
            });
            document.dispatchEvent(ce);
        } else {
            $.ajax(params);
        }
    }

    function initFormArgs() {
        var args = this.global.http.requestArgs.mergeArray(this.content.requestArgs);
        for (var key in args) {
            var temp = this.doc.id + ':args:' + args[key].name;
            var value = localStorage.getItem(temp);
            if (value) {
                args[key].tempValue = value;
            } else {
                if (args[key].defaultValue) {
                    args[key].tempValue = args[key].defaultValue;
                }
            }
            args[key].id = utils.generateUID();
        }
        this.formArgs = args;
    }

    function initFormHeaders() {
        var headers = this.global.http.requestHeaders.mergeArray(this.content.requestHeaders);
        for (var key in headers) {
            var temp = this.doc.id + ':headers:' + headers[key].name;
            var value = localStorage.getItem(temp);
            if (value) {
                headers[key].tempValue = value;
            } else {
                if (headers[key].defaultValue) {
                    headers[key].tempValue = headers[key].defaultValue;
                }
            }
            headers[key].id = utils.generateUID();
        }
        this.formHeaders = headers;
    }

    function initUrlArgs() {
        var urlArgs = [];
        var match = this.content.url.match(/(\{[a-zA-Z0-9_]+\})/g);
        if (match != null && match.length > 0) {
            urlArgs = match;
            urlArgs = urlArgs.map(function (d) {
                return {name: d.substring(1, d.length - 1), tempValue: null, id: utils.generateUID()};
            });
        }
        this.urlArgs = urlArgs;
    }

    new Vue({
        el: '#docApp',
        data: {
            hasXyjPlugin: false,
            editing: false,
            doc: doc,
            fileAccess: null,
            attachs: null,
            content: {},
            global: null,
            apiLoading: false,
            resultActive: 'content',
            currentEnv: null,
            formHeaders: [],
            formArgs: [],
            urlArgs: [],
            result: {
                content: '',
                resultHeaders: '',
                resultRunTime: '',
                resultStatusCode: ''
            },
            algorithms: [
                {
                    name: 'BASE64', fn: function (value) {
                    return window.btoa(value);
                }
                }
            ]
        },
        mounted: function () {
            initAceEditor(this.content.dataType, this);
            var self = this;
            window.setTimeout(function () {
                var attr = $('body').attr('data-ext-version');
                if (attr && attr.localeCompare && attr.localeCompare('1.4.2') >= 0) {
                    self.hasXyjPlugin = true;
                }
            });
        },
        created: function () {
            this.content = utils.toJSON(doc.content);
            if (!this.content) {
                this.content = {};
            }
            this.loadAttach();
            var g = projectGlobal;
            if (!g.environment) {
                g.environment = [];
            } else {
                g.environment = utils.toJSON(g.environment);
            }
            if (!g.http) {
                g.http = {};
            } else {
                g.http = utils.toJSON(g.http);
            }

            if (!g.http.requestHeaders) {
                g.http.requestHeaders = [];
            } else {
                g.http.requestHeaders = utils.toJSON(g.http.requestHeaders);
            }
            if (!g.http.responseHeaders) {
                g.http.responseHeaders = [];
            } else {
                g.http.responseHeaders = utils.toJSON(g.http.responseHeaders);
            }
            if (!g.http.requestArgs) {
                g.http.requestArgs = [];
            } else {
                g.http.requestArgs = utils.toJSON(g.http.requestArgs);
            }
            if (!g.http.responseArgs) {
                g.http.responseArgs = [];
            } else {
                g.http.responseArgs = utils.toJSON(g.http.responseArgs);
            }
            if (!this.content.url) {
                this.content.url = '';
            }

            this.global = g;
            new Clipboard('.content-copy');
            this.currentEnv = g.environment[0] || {};

            initUrlArgs.call(this);
            window.content =this.content;
            window.urlArgs =this.urlArgs;

            initFormHeaders.call(this);
            initFormArgs.call(this);

        },
        computed: {
            requestURL: {
                get: function () {
                    console.log('requestURL');
                    var urlArgs= this.urlArgs;
                    var temp = this.content.url;
                    if (!temp) {
                        temp = ''
                    } else {
                        if (this.currentEnv && this.currentEnv.vars) {
                            this.currentEnv.vars.forEach(function (item) {
                                var reg = new RegExp('\\$' + item.name + '\\$', 'g');
                                temp = temp.replace(reg, item.value);
                            });
                        }
                        if (urlArgs && urlArgs.length > 0) {
                            urlArgs.forEach(function (item) {
                                var name = '{' + item.name + '}';
                                var reg = new RegExp(name, 'g');
                                temp = temp.replace(reg, item.tempValue || name)
                            });
                        }
                    }
                    /*setTimeout(function(){

                     },500);*/
                    //$('#requestURL').val(temp);

                    return temp;
                },
                set:function(v){
                    console.log(v)
                }
            },
            requestArgsPreview: function () {
                var type = this.content.dataType;
                var obj = getRequestArgsObject(this.formArgs);
                if (type == 'XML') {
                    obj = {xml: obj};
                    return formatXml(xml.writeXML(obj));
                } else if (type == 'JSON') {
                    if (obj) {
                        return JSON.stringify(obj, null, '\t');
                    }
                    return '{}';
                }
                return 'data not support';
            }
        },
        methods: {
            loadAttach: function () {
                var self = this;
                utils.get('/attach/' + this.doc.id, {projectId:_projectId_}, function (rs) {
                    self.attachs = rs.data.attachs || [];
                    self.fileAccess = rs.data.fileAccess || '';
                });
            },
            proxySubmit: function () {
                apiSubmit.call(this, 'proxy');
            },
            localSubmit: function () {
                apiSubmit.call(this);
            },
            pluginSubmit: function () {
                apiSubmit.call(this, 'plugin');
            },
            algorithmClick: function (index, fn, data) {
                var temp = data[index];
                temp.tempValue = fn(temp.tempValue);
                data.splice(index, 1, temp)
            },
            openNewWindow: function () {
                var win = window.open('', 'new');
                win.document.documentElement.innerHTML = '';
                win.document.write(utils.unescape(this.result.content));
                win.document.close();
            }
        }
    });


    //初始化ace编辑器
    function initAceEditor(type, self) {
        var mode;
        if (type == 'JSON') {
            mode = 'ace/mode/json';
        } else if (type == 'XML') {
            mode = 'ace/mode/xml';
        }
        setTimeout(function () {
            try {
                var aceeditor = ace.edit("ace-editor-box");
                window.aceeditor = aceeditor;
            } catch (e) {
                aceeditor = {
                    getValue: function () {
                        return '';
                    },
                    setTheme: function () {
                    },
                    session: {
                        setMode: function () {
                        }
                    },
                    setValue: function () {
                    }
                };
            }
            aceeditor.setTheme("ace/theme/chrome");
            aceeditor.session.setMode(mode);
            aceeditor.setValue(self.requestArgsPreview);
        }, 300);
    }

    function getRequestArgsObject(data) {
        var obj = {};
        data.forEach(function (d) {
            var name = d.name;
            if(d.children && d.children.length>0){
                obj[name]=getRequestArgsObject(d.children);
            }else{
                switch (d.type) {
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
                        obj[name] = [0, 1];
                        break;
                    case 'array[boolean]':
                        obj[name] = [true];
                        break;
                    case 'array[string]':
                        obj[name] = [''];
                        break;
                    case 'array[object]':
                        obj[name] = [getRequestArgsObject({}, d.children)];
                        break;
                    case 'array[array]':
                        obj[name] = [[]];
                        break;
                    default:
                        obj[name] = '';
                        break;
                }
            }
        });
        return obj;
    }
});