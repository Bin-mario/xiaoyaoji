/**
 * User: zhoujingjie
 * Date: 17/5/30
 * Time: 16:20
 */

(function(){
    define(['utils'],function(utils){
        return {
            //template:document.getElementById('response-headers-template').innerHTML,
            //props:['responseHeaders','editing','name'],
            mounted:function(){
                this.$on('sortUpdate',function(o){
                    function findParent(arr){
                        for(var i=0;i<arr.length;i++){
                            if(arr[i].id === o.id){
                                return arr;
                            }
                            if(arr[i].children && arr[i].children.length>0){
                                return findParent(arr[i].children);
                            }
                        }
                    }
                    var parent = findParent(this[this.name]);
                    if(parent){
                        parent.move(o.oldIndex,o.index);
                    }
                });
            },
            data:function(){
                return {parent:null}
            },
            methods: {
                removeRow: function (item, data) {
                    var index = data.indexOf(item);
                    data.splice(index,1)
                },
                dragstart:function(parent){
                    this.parent=parent;
                    console.log(this)
                },
                insertRow:function(item){
                    if(!this.name || this.name.indexOf('Args')){
                        item.children.push({id:utils.generateUID(),require: 'true',type:'string', children: []});
                    }else{
                        item.children.push({id:utils.generateUID(),require: 'true', children: []});
                    }
                    _initsort_(this.$root);
                },
                apiArgsColumnFold:function(e){
                    var $dom = $(e.target);
                    var $next =$(e.target).parent().parent().parent().next();
                    if($dom.hasClass('open')){
                        $dom.removeClass('open');
                        $next.slideUp();
                    }else{
                        $dom.addClass('open');
                        $next.slideDown();
                    }
                }
            }
        }
    });
})();