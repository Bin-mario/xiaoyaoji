$(function(){
    requirejs(['vue','utils',x.ctx+'/assets/html5sortable/html.sortable.min.js'],function(Vue,utils,sortable){

        function setDocContentHeight(){
            var $d=$('#doc-names');
            var top = $d.offset().top -$(window).scrollTop();
            var wHeight=$(window).height();
            var height = wHeight-top - $('.dl-placehoder').height();
            $d.height(height);
        }
        //加载
        function loadDoc(url){
            var $docContent =$('#doc-content');
            $docContent.html("");
            $('#loading').show();
            $.get(x.ctx+url,{_t:Date.now()},function(rs){
                $('#loading').hide();
                $docContent.html(rs);
                history.pushState('','',x.ctx+url);
            });
        }

        $(window).on({
            resize:setDocContentHeight,
            popstate:function(){
                loadDoc(location.pathname.substring(x.ctx.length));
            }
        });
        window.addEventListener('message', function (e) {
            if(e.data && e.data.type==='projects'){
                app.projects = e.data.data;
            }
        });
        var app = new Vue({
            el:'#docLeft',
            data:{
                ctx:x.ctx,
                menu:{
                    show:false,
                    top:0,
                    left:0,
                    isFolder:true
                },
                copiesProjectId:_projectId_, //复制的项目id
                projects:[],
                searchText:'',
                showSearch:false,
                edit:window._edit_,
                target:{},
                createModal:{type:'',value:'',id:''},
                searchResults:[]
            },
            mounted:function(){
                var self = this;
                $("body").on("click",function(){
                    self.menu.show=false;
                });
                setDocContentHeight();
                if(this.edit){
                    sortable('.dl-docs', 'destroy');
                    var doms = sortable('.dl-docs',{
                        connectWith:'dl-docs-sub',
                        items:'.name-item'
                    });

                    $(doms[0]).off('sortupdate').on('sortupdate', function(e) {
                        e.detail = e.originalEvent.detail;
                        var $parent = $(e.detail.endparent);
                        var $item =$(e.detail.item);
                        var $lis = $parent.children('li');
                        var sorts = [];
                        $lis.each(function(index,item){
                            sorts.push(index+"_"+$(item).data("id"));
                        });
                        utils.post('/doc/sort',{
                            id:$item.data('id'),
                            parentId:$parent.data('id') || 0,
                            sorts:sorts.toString()
                        },function(){
                            toastr.success('操作成功')
                        })
                    });
                }
                    //显示菜单
                $('.doc-name.active').parents('ul').each(function(){
                    $(this).removeClass('hide');
                    $(this).prev().find(".el-tree-expand").addClass("expanded");
                });
            },
            methods:{
                itemClick:function(url,e){
                    if(!history.pushState){
                        return false;
                    }
                    e.preventDefault();

                    $('.doc-name.active').removeClass('active');
                    $(e.target).parent().addClass('active');

                    loadDoc(url);
                },
                fold:function(e){
                    if(e.target instanceof HTMLAnchorElement){
                        return true;
                    }
                    var $target = $(e.currentTarget);
                    if($target.hasClass('folder')){
                        var $child = $target.find('.el-tree-expand');
                        if($child.hasClass('expanded')){
                            $target.next().slideUp();
                            $child.removeClass('expanded');
                        }else{
                            $target.next().slideDown();
                            $child.addClass('expanded');
                        }
                    }
                    e.stopPropagation();
                    e.preventDefault();
                },
                createFn:function(type,parentId){
                    this.createModal.display=true;
                    this.createModal.value='';
                    this.target.type=type;
                    if(parentId){
                        this.target.id = parentId;
                    }
                },
                copyDoc:function(){
                    if(this.target.id){
                        var self =this;
                        utils.post('/doc/copy',{
                            docId:self.target.id,
                            projectId:_projectId_,
                            toProjectId:self.copiesProjectId
                        },function(){
                            location.reload();
                        });
                    }else{
                        toastr.error('未选中文档');
                    }
                },
                updateName:function(){
                    this.createModal.display=true;
                    this.createModal.id=this.target.id;
                    this.createModal.value=this.target.name;
                },
                createSubmit:function(){
                    //更新
                    if(this.createModal.id){
                       utils.post('/doc/'+this.createModal.id,{name:this.createModal.value},function(){
                           location.reload();
                       });
                    }else{
                        //新增
                        utils.post('/doc/',{name:this.createModal.value,parentId:this.target.id,projectId:window._projectId_,type:this.target.type},function(rs){
                            location.href=x.ctx+"/doc/"+rs.data+"/edit";
                        });
                    }
                },
                contextMenu:function(e){
                    if(!this.edit){
                        return true;
                    }
                    var offset=$('#doc-names').offset();
                    //距离最顶部高度
                    var $target=$(e.target).parents('li:eq(0)');
                    //滚动条高度
                    var scrollTop = $(document).scrollTop();
                    //菜单高度
                    var menuHeight =$('#dl-menus').height();
                    //窗口大小
                    var winHeight = $(window).height();
                    this.target = {
                        type:$target.data("type"),
                        id:$target.data("id"),
                        name:$target.data("name")
                    };
                    if(this.target.type==='sys.folder'){
                        this.menu.isFolder=true;
                    }else{
                        this.menu.isFolder=false;
                    }
                    var top = e.pageY - scrollTop;
                    //判断最底下
                    if(menuHeight + top > winHeight){
                        top = winHeight -menuHeight;
                    }

                    this.menu.top = top;
                    this.menu.left = e.pageX - offset.left;
                    this.menu.show= true;
                },
                deleteDoc:function(){
                    var self=this;
                    UIkit.modal.confirm('是否确认删除?一旦删除不可恢复').then(function(){
                        utils.delete('/doc/'+self.target.id,function(){
                            location.href=x.ctx+'/project/'+window._projectId_+"/edit";
                        })
                    });
                },
                search:function(){
                    var text= this.searchText;
                    if(text){
                        var self=this;
                        utils.get('/doc/search',{projectId:window._projectId_,text:text},function(rs){
                            self.searchResults = rs.data.docs;
                            self.showSearch=true;
                        });
                    }else{
                        this.showSearch=false;
                    }
                }
            }
        })
    })
});