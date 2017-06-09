require(['utils','vue'],function(utils,Vue){
    new Vue({
        el:'#app',
        methods:{
            logout:function(){
                utils.logout();
                location.reload();
            },
            qq: function () {
                utils.login.qq();
            },
            weibo:function(){
                utils.login.weibo();
            },
            github:function(){
                utils.login.github();
            }
        }
    });    
});
