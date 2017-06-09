/**
 * User: zhoujingjie
 * Date: 17/4/25
 * Time: 21:37
 */

(function(){
    requirejs(['vue','utils',ctx+'/assets/js/project/doc.commons.js'],function(Vue,utils,commons){

        window.submitProjectGlobal = function(){
            var environment = JSON.stringify(app.global.environment);
            var http = JSON.stringify(app.global.http);
            utils.post('/project/global/'+app.global.projectId,{
                environment:environment,
                http:http,
                status:JSON.stringify(app.global.status)
            },function(rs){
                toastr.success('操作成功');
            });
        };

        var app =new Vue({
            el:'#global',
            data:{
                uitab:'http',
                editing:true,
                responseArgs:[],
                import:null,
                importValue:null,
                importModal:false,
                envModal:false,
                flag:{
                    tempEnv:null,
                    varname:'$变量$',
                    tab:'body',
                    headers:commons.headers,
                    requests: commons.requests,
                    responses: commons.responses
                },
                global:null,
                tempStatus:{}
            },
            mounted:function(){
                commons._initsort_(this);
            },
            created:function(){
                var g = window.global;
                if(!g){
                    g={};
                }
                if(!g.http){
                    g.http={};
                }else{
                    g.http = utils.toJSON(g.http);
                }
                if(!g.http.requestHeaders){
                    g.http.requestHeaders=[];
                }else{
                    g.http.requestHeaders=utils.toJSON(g.http.requestHeaders);
                }
                if(!g.http.responseHeaders){
                    g.http.responseHeaders=[];
                }else{
                    g.http.responseHeaders=utils.toJSON(g.http.responseHeaders);
                }
                if(!g.http.requestArgs){
                    g.http.requestArgs=[];
                }else{
                    g.http.requestArgs=utils.toJSON(g.http.requestArgs);
                }
                if(!g.http.responseArgs){
                    g.http.responseArgs=[];
                }else{
                    g.http.responseArgs=utils.toJSON(g.http.responseArgs);
                }

                commons.checkId(g.http.requestArgs);
                commons.checkId(g.http.requestHeaders);
                commons.checkId(g.http.responseHeaders);
                commons.checkId(g.http.responseArgs);

                if(!g.environment){
                    g.environment=[];
                }else{
                    g.environment=utils.toJSON(g.environment);
                }
                if(!g.status){
                    g.status=[];
                }else{
                    g.status = utils.toJSON(g.status);
                }
                this.global = g;
            },
            methods:{
                createEnv:function(){
                    this.flag.tempEnv={vars:[{}]};
                    this.envModal=true;
                },
                envNewLine: function (index) {
                    if (index == this.flag.tempEnv.vars.length - 1) {
                        this.flag.tempEnv.vars.push({});
                    }
                },
                envEdit: function (item) {
                    this.envModal = true;
                    this.flag.tempEnv=item;
                },
                envSave: function () {
                    var self = this;
                    if (!this.flag.tempEnv.name) {
                        toastr.error('请输入环境名称');
                        return false;
                    }
                    if (this.flag.tempEnv.vars) {
                        this.flag.tempEnv.vars = this.flag.tempEnv.vars.filter(function (item) {
                            return item.name != undefined && item.name != null && item.name != '';
                        });
                    }
                    if (!this.flag.tempEnv) {
                        this.flag.tempEnv = {vars:[]};
                    }
                    //表示修改
                    if (this.flag.tempEnv.t) {
                        var t = this.flag.tempEnv.t;
                        var index = this.flag.tempEnv.vars.findIndex(function (item) {
                            return item.t == t;
                        });
                        if (index != -1) {
                            this.global.environment.$set(index, this.flag.tempEnv);
                        }
                    } else {
                        this.flag.tempEnv.t=Date.now();
                        this.global.environment.push(this.flag.tempEnv);
                    }
                    this.global.environment = this.global.environment.map(function (item) {
                        return {name: item.name, t: item.t, vars: item.vars}
                    });
                    this.envModal= false;
                },
                envRemove: function () {
                    var self = this;
                    var t = this.flag.tempEnv.t;
                    var index = this.envs.findIndex(function (item) {
                        return item.t == self.flag.tempEnv.t;
                    });
                    this.envs.$remove(this.envs[index]);
                    utils.post('/project/' + this.id + '.json', {environments: JSON.stringify(this.envs)}, function (rs) {
                        toastr.success('移除成功');
                        if (self.envs.length == 0) {
                            self.currentEnv = {name: '环境切换', vars: []};
                        } else {
                            if (self.currentEnv.t == t) {
                                self.currentEnv = self.envs[0];
                            }
                        }
                    });
                },
                envCopy: function (item) {
                    var temp = $.extend(true, {}, item);
                    temp.t = Date.now();
                    this.global.environment.push(temp);
                },
                newRow:function(type){
                    if(type =="requestHeaders"){
                        this.global.http.requestHeaders.push({require:'true',children:[]});
                    }else if(type =="requestArgs"){
                        this.global.http.requestArgs.push({require:'true',children:[],type:'string'});
                    }else if(type =="responseHeaders"){
                        this.global.http.responseHeaders.push({require:'true',children:[]});
                    }else if(type =="responseArgs"){
                        this.global.http.responseArgs.push({require:'true',children:[],type:'string'});
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
                        if (self.import == 'requestArgs') {
                            self.global.http.requestArgs.push(d);
                        } else if (self.import == 'requestHeaders') {
                            self.global.http.requestHeaders.push(d);
                        } else if (self.import == 'responseArgs') {
                            self.global.http.responseArgs.push(d);
                        }else if(self.import == 'responseHeaders'){
                            self.global.http.responseHeaders.push(d);
                        }
                    });
                    this.importModal = false;
                    commons._initsort_(this);
                },
                statusOk:function(){
                    if(!(this.tempStatus.name)){
                        return false;
                    }
                    if(this.tempStatus.t){

                    }else{
                        this.tempStatus.t = Date.now();
                        this.global.status.push(this.tempStatus);
                    }
                }
            }
        });
    });

})();
