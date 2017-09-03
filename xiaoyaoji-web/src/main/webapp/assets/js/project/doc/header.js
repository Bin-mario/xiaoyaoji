(function(){
    require(['vue'],function(Vue){
        //传递消息给sidebar.js 
        function pushMessage(method,args){
            window.postMessage({type:'event',method:method,args:args},'*')
        }
        var app = new Vue({
            el:'#xd-header',
            methods:{
                sidebar:function(method){
                    var newArgs=[];
                    if(arguments.length>1){
                        for(var i=1;i<arguments.length;i++){
                            newArgs[i-1] = arguments[i];
                        }
                    }
                    pushMessage(method,newArgs);
                }
            }
        })
    })
})();