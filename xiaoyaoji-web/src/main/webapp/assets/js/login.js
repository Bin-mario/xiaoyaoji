require(['utils','vue', 'vueEx'], function (utils, Vue) {
    

    function qqlogin(openId, accessToken) {
        utils.post('/login/qq.json', {openId: openId, accessToken: accessToken}, function (rs) {
            location.href = utils.config.ctx + '/dashboard';
        });
    }

    new Vue({
        el: '#login',
        data: {password: '', email: '', params: {}, remember: false,from:null},
        created: function () {
            if (location.search) {
                this.params = utils.getQueryParams(location.search);
                this.from = this.params['f'];
                switch (this.params['status']) {
                    case "expired":
                        toastr.warning('会话已过期');
                        break;
                    case "success":
                        toastr.success('操作成功');
                        break;
                }
            }
        },
        methods: {
            submit: function () {
                var self = this;
                this.$validator.validateAll({email:this.email,password:this.password}).then(function(){
                    utils.post('/login', {
                        email: self.email, password: self.password
                    }, function (rs) {
                        utils.login.success(rs.data.token, rs.data.user, self.from);
                    });
                }).catch(function(){
                })
            },
            qq: function () {
                utils.login.qq();
            },
            weibo: function () {
                utils.login.weibo();
            },
            github: function () {
                utils.login.github();
            }
        }
    });

});