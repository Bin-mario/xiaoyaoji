$(function(){
    require(['utils','vue'],function(utils,Vue){
        new Vue({
            el:'#doc-header',
            data:{
                submitComment:'',
                projectName:_projectName_,
                history:[],
                g:{
                    ctx:ctx,
                    edit:_edit_
                }
            },
            watch:{
                "projectName":function(){
                    if(this.projectName!=_projectName_){
                        utils.post('/project/'+_projectId_,{name:this.projectName},function(rs){
                            toastr.success('修改成功');
                        });
                    }
                }
            },
            mounted:function(){
                var self=this;
                if(_docId_ && $('#doc-history-li').size()>0){
                    utils.get('/doc/history/'+_docId_,{},function(rs){
                        self.history = rs.data;
                    });
                }
                $('body').on('keydown',function(e){
                    //ctrl+s or command+s
                    if((e.ctrlKey || e.metaKey) && e.which == 83 ){
                        e.preventDefault();
                        self.submit();
                        return false;
                    }
                })
            },
            methods:{
                historyURL:function(docId,isEdit,historyId){
                    //g.ctx+'/doc/'+item.docId+(g.edit)?'/edit':''+'?docHistoryId='+item.id
                    if(isEdit){
                        return ctx+'/doc/'+docId+'/edit?docHistoryId='+historyId;
                    }
                    return ctx+'/doc/'+docId+'?docHistoryId='+historyId;
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
                }
            }
        })
    })
});