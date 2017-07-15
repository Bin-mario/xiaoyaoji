$(function(){
    require(['vue','utils'],function(Vue,utils){
        window.Vue=Vue;
        new Vue({
            el:'#appmain',
            data:{
                name:'',
                description:'',
                permission:'PUBLIC'
            },
            mounted:function(){
                $('button[uk-close]>svg').remove();
            },
            methods:{
                importFile:function(pluginId,e){
                    if(e.target.files.length === 0){
                        toastr.error('请选择文件');
                        return;
                    }
                    var file = e.target.files[0];
                    var fd = new FormData();
                    fd.append('file', file);
                    fd.append('pluginId', pluginId);
                    utils.fileloader('/project/import', fd,
                        function (rs) {
                            location.reload();
                        });
                },
                newProject:function(){
                    utils.post('/project',{name:this.name,description:this.description,permission:this.permission},function(rs){
                        location.href=ctx+'/doc/'+rs.data.docId+'/edit';
                    });
                },
                rename:function(id,name){
                    UIkit.modal.prompt('请输入新的名称',name).then(function(rs){
                        if(rs && rs != name){
                            utils.post('/project/'+id,{name:rs},function(){location.reload();});
                        }
                    });
                },
                deleteProject:function (id) {   //
                    UIkit.modal.confirm('是否确认删除?').then(function() {
                        utils.delete('/project/'+id,function () {
                            location.reload();
                        });
                    }, function () {
                    });
                },
                deleteActual:function (id) {  //彻底删除
                    UIkit.modal.confirm('是否确认删除,一旦删除无法恢复?').then(function() {
                        utils.delete('/project/'+id+"/actual",function () {
                            location.reload();
                        });
                    }, function () {
                    });
                },
                restore:function(id){    //还原
                    utils.post('/project/'+id,{status:'VALID'},function(){
                        location.href=ctx+'/dashboard'
                    });
                }
            }
        });
    });


});