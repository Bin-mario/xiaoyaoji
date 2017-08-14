$(function(){
    require(['utils','vue'],function(utils,Vue){
        new Vue({
            el:'#sidebar',
            data:{
                submitComment:'',
                projectName:_projectName_,
                history:[],
                g:{
                    ctx:ctx,
                    edit:_edit_
                },
                projects:[],
                loading:{
                    project:null,
                    history:null
                }
            },
            watch:{
                "projectName":function(){
                    if(this.projectName!==_projectName_){
                        utils.post('/project/'+_projectId_,{name:this.projectName},function(rs){
                            toastr.success('修改成功');
                        });
                    }
                }
            },
            mounted:function(){
                var self=this;

                $('body').on('keydown',function(e){
                    //ctrl+s or command+s
                    if((e.ctrlKey || e.metaKey) && e.which === 83 ){
                        e.preventDefault();
                        self.submit();
                        return false;
                    }
                });

                this.loadProjects();
            },
            methods:{
                loadHistory:function(){
                    if(_docId_){
                        var self = this;
                        self.loading.history=true;
                        utils.get('/doc/history/'+_docId_,{},function(rs){
                            self.loading.history=false;
                            self.history = rs.data;
                        });
                    }
                },
                switchCommonly:function(item){
                    if(item.commonlyUsed=='YES'){
                        item.commonlyUsed='NO';
                        utils.post('/project/'+ item.id + '/commonly.json',{isCommonlyUsed:'NO'});
                    } else {
                        item.commonlyUsed='YES';
                        utils.post('/project/'+ item.id + '/commonly.json',{isCommonlyUsed:'YES'});
                    }

                },
                editpage:function(){
                    location.href=window.ctx+'/doc/'+window._docId_+'/edit';
                },
                viewpage:function(){
                    location.href=window.ctx+'/doc/'+window._docId_;
                },
                historyURL:function(docId,isEdit,historyId){
                    if(isEdit){
                        return location.path+'/doc/'+docId+'/edit?docHistoryId='+historyId;
                    }
                    return location.path+'/doc/'+docId+'?docHistoryId='+historyId;
                },
                showProject:function(){
                    $('#sidebar').addClass('layer');
                    this.loadProjects();
                },
                submit:function(){
                    if(_isGlobal_){
                        window.submitProjectGlobal();
                    }else{
                        var doc = window.getDoc();
                        var url =  '/doc/'+_docId_;
                        utils.post(url,{name:doc.name,comment:this.submitComment,content:doc.content},function(){
                            toastr.success('操作成功');
                        });
                    }
                },
                loadProjects:function(){
                    var self = this;
                    self.loading.project=true;
                    utils.get('/project/list',{},function(rs){
                        self.loading.project=false;
                        self.projects = rs.data.projects;
                    });
                }
            }
        })
    })
});