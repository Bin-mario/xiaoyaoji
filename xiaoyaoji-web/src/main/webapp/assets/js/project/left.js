$(function(){
    requirejs(['vue','utils',ctx+'/assets/html5sortable/html.sortable.min.js'],function(Vue,utils,sortable){

        function setDocContentHeight(){
            var $d=$('#doc-names');
            var top = $d.offset().top -$(window).scrollTop();
            var wHeight=$(window).height();
            var height = wHeight-top - $('.dl-placehoder').height();
            $d.height(height);
        }

        $(window).resize(setDocContentHeight);

        new Vue({
            el:'#docLeft',
            data:{
                ctx:ctx,
                menu:{
                    show:false,
                    top:0,
                    left:0,
                    isFolder:true
                },
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
                        /*

                         This event is triggered when the user stopped sorting and the DOM position has changed.

                         e.detail.item contains the current dragged element.
                         e.detail.index contains the new index of the dragged element (considering only list items)
                         e.detail.oldindex contains the old index of the dragged element (considering only list items)
                         e.detail.elementIndex contains the new index of the dragged element (considering all items within sortable)
                         e.detail.oldElementIndex contains the old index of the dragged element (considering all items within sortable)
                         e.detail.startparent contains the element that the dragged item comes from
                         e.detail.endparent contains the element that the dragged item was added to (new parent)

                         */
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
                            location.href=ctx+"/doc/"+rs.data+"/edit";
                        });
                    }
                },
                contextMenu:function(e){
                    if(!this.edit){
                        return true;
                    }
                    var offset=$('#doc-names').offset();
                    var $target=$(e.target).parents('li:eq(0)');
                    this.target = {
                        type:$target.data("type"),
                        id:$target.data("id"),
                        name:$target.data("name")
                    };
                    if(this.target.type=='sys.folder'){
                        this.menu.isFolder=true;
                    }else{
                        this.menu.isFolder=false;
                    }
                    //50 = 搜索框 height
                    var docLeftHeight=($('#doc-left').height() || 0);
                    var top =e.pageY-offset.top+50+docLeftHeight ;
                    var menuHeight =$('#dl-menus').height();
                    if(top + menuHeight+70>=$(window).height()){
                        top = $(window).height() -menuHeight-50-docLeftHeight;
                    }
                    this.menu.top = top;
                    this.menu.left = e.pageX - offset.left;
                    this.menu.show= true;
                },
                deleteDoc:function(){
                    var self=this;
                    UIkit.modal.confirm('是否确认删除?一旦删除不可恢复').then(function(){
                        utils.delete('/doc/'+self.target.id,function(){
                            location.href=ctx+'/project/'+window._projectId_+"/edit";
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