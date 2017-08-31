require(['vue', 'utils','vueEx'], function (Vue, utils) {
    new Vue({
        el: '#app',
        data: {password: null, rePassword: null, email: null, id: null},
        mounted:function(){
            if (location.search) {
                var match = location.search.match(/token=([\d|\w]+)/);
                if (match && match.length == 2) {
                    var token = match[1];
                    if (token) {
                        var d = atob(token).split("!");
                        this.id = d[0];
                        this.email = d[1];
                        return true;
                    }
                }
            }
            toastr.error('无效地址');
        },
        methods: {
            submit: function () {
                if (!this.password || this.password != this.rePassword) {
                    toastr.error('两次密码输入不一致');
                    return;
                }
                utils.post('/user/newpassword.json', {
                    email: this.email, password: this.password, id: this.id
                }, function (rs) {
                    toastr.success('操作成功!');
                    setTimeout(function(){
                        location.href = x.ctx + '/login';
                    },1000);
                });
            }
        }
    });
});