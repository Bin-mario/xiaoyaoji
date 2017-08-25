require(['utils','vue', 'vueEx'], function (utils, Vue) {
    

    var app = new Vue({
        el: '#login',
        data: {password: '', email: '', params: {}, remember: false,from:null},
        created: function () {
            if (location.search) {
                this.params = utils.getQueryParams(location.search);
                this.from = this.params['refer'];
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
                        utils.login.success(self.from);
                    });
                }).catch(function(){
                })
            },
            thirdparty:function(pluginId,openURL){
                window.open(openURL, pluginId, 'height=550, width=900, top=0, left=0, toolbar=no, menubar=no, scrollbars=no,resizable=no,location=no, status=no');

                if (window.initialized) {
                    return true;
                }
                window.initialized = true;
                window.addEventListener('message', function (e) {
                    var data = e.data;
                    data = JSON.parse(data);
                    utils.post('/login/plugin?pluginId=' + pluginId, data, function (rs) {
                        utils.login.success( null);
                    });
                });
            }
        }
    });

});