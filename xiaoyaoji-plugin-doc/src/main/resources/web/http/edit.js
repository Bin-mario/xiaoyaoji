(function(){
    var thirds = [
        'vue',
        'utils',
        ctx+'/assets/js/project/doc.commons.js'
    ];
    requirejs(thirds,function(Vue,utils,commons){




        var docApp = new Vue({
            el:'#doc',
            data:{
                editing:true,
                doc:doc,
                responseArgs:[],
                import:null,
                importValue:null,
                importModal:false,
                currentEnv:null,
                urlArgs:[],
                flag:{
                    resp:'body',
                    tab:'body',
                    headers:commons.headers,
                    requests: commons.requests,
                    responses: commons.responses
                },
                content:null,
                fileAccess:null,
                attachs:null,
                global:null
            },
            created:function(){
                if(!this.doc.content){
                    this.doc.content={};
                }

                var content = utils.toJSON(this.doc.content);
                if(!content.requestMethod){
                    content.requestMethod = 'GET';
                }
                if(!content.dataType){
                    content.dataType = 'X-WWW-FORM-URLENCODED';
                }
                if(!content.contentType){
                    content.contentType = 'JSON';
                }
                if(!content.requestArgs){
                    content.requestArgs=[];
                }
                if(!content.requestHeaders){
                    content.requestHeaders=[];
                }
                if(!content.responseHeaders){
                    content.responseHeaders=[];
                }
                if(!content.responseArgs){
                    content.responseArgs=[];
                }
                if(!content.url){
                    content.url='';
                }

                commons.checkId(content.requestArgs);
                commons.checkId(content.requestHeaders);
                commons.checkId(content.responseHeaders);
                commons.checkId(content.responseArgs);
                
                this.content = content;
                this.loadAttach();

                var g= projectGlobal;
                if(!g.status){
                    g.status=[];
                }else{
                    g.status = utils.toJSON(g.status);
                }
                if(!g.environment){
                    g.environment = [];
                }else{
                    g.environment = utils.toJSON(g.environment);
                }

                if(!content.status){
                    var status = '';
                    if(g.status[0]){
                        status = g.status[0].name;
                    }
                    content.status = status;
                }
                this.global = g;

                _initsort_(this);
                
                this.currentEnv = g.environment[0] || {};
                var urlArgs=[];
                var match = this.content.url.match(/(\{[a-zA-Z0-9_]+\})/g);
                if (match != null && match.length > 0) {
                    urlArgs = match;
                    urlArgs = urlArgs.map(function (d) {
                        return {name: d.substring(1, d.length - 1), value: null};
                    });
                }
                this.urlArgs= urlArgs;
            },
            computed:{
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
                    $('#requestURL').val(temp);
                    return temp;
                }
            },
            methods:{
                newRow:function(type){
                    if(type =='requestHeader'){
                         this.content.requestHeaders.push({require:'true',children:[]});
                    }else if(type =='requestArg'){
                        this.content.requestArgs.push({require:'true',children:[],type:'string'});
                    }else if(type =='responseHeader'){
                        this.content.responseHeaders.push({require:'true',children:[]});
                    }else  if(type =='responseArg'){
                        this.content.responseArgs.push({require:'true',children:[],type:'string'});
                    }
                    commons._initsort_(this);
                },
                importJSON:function(type){
                    this.importModal = true;
                    this.import = type;
                 },
                importOk:function(){
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
                    commons.parseImportData(data, temp);
                    var self = this;
                    temp.forEach(function (d) {
                        if(self.import =='requestHeader'){
                            self.content.requestHeaders.push(d);
                        }else if(self.import =='requestArg'){
                            self.content.requestArgs.push(d);
                        }else if(self.import =='responseHeader'){
                            self.content.responseHeaders.push(d);
                        }else if(self.import =='responseArg'){
                            self.content.responseArgs.push(d);
                        }
                    });
                    this.importModal = false;
                    commons._initsort_(this);
                },
                loadAttach:function(){
                    var self = this;
                    utils.get('/attach/'+this.doc.id,{projectId:_projectId_},function (rs) {
                        self.attachs = rs.data.attachs || [];
                        self.fileAccess = rs.data.fileAccess || '';
                    });
                },
                apiVarsClick:function(name){
                    this.content.url += '$'+name+'$';
                },
                deleteFile:function(item){
                    if(!confirm('是否确认删除')){
                        return;
                    }
                    var self=this;
                    utils.delete('/attach/'+item.id+"?projectId="+_projectId_,function(rs){
                        self.attachs.$remove(item);           
                    })
                },
                fileUpload:function(e){
                    var files = e.target.files;
                    if(files.length == 0)
                        return false;
                    var fd = new FormData();
                    fd.append('relateId',this.doc.id);
                    for(var i=0;i<files.length;i++){
                        fd.append('file',files[i]);
                    }
                    fd.append("projectId",_projectId_);
                    var self = this;
                    utils.fileloader('/attach',fd,function(){
                        self.loadAttach();
                    });
                }
            }
        });

        window.getDoc = function(){
            var description = $('#api-description').html();
            docApp.content.description = description;
            var content = JSON.stringify(docApp.content);
            return {
                name:docApp.doc.name,
                content:content
            };
        }

    });

})();
