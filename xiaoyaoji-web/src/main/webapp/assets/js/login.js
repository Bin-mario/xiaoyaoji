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
            thirdparty:function(pluginId,callback){
                var url = 'https://graph.qq.com/oauth2.0/authorize?response_type=code&state=login&client_id=101333549&redirect_uri='+callback;
                window.open(url, pluginId, 'height=550, width=900, top=0, left=0, toolbar=no, menubar=no, scrollbars=no,resizable=no,location=no, status=no');

                if (window.initialized) {
                    return true;
                }
                window.initialized = true;
                window.addEventListener('message', function (e) {
                    var data = e.data;
                    data = JSON.parse(data);
                    utils.post('/login/plugin/' + pluginId, data, function (rs) {
                        utils.login.success(rs.data.token, rs.data.user, null);
                    });
                });
            }
        }
    });

});