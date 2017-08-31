/**
 * User: zhoujingjie
 * Date: 16/8/20
 * Time: 22:13
 */
require(['utils', 'vue', 'vueEx'], function (utils, Vue) {
    new Vue({
        el: '#register',
        data: {password: null, nickname: null, email: null},
        methods: {
            submit: function () {
                var self =this;
                this.$validator.validateAll({email:self.email,password:self.password,nickname:self.nickname}).then(function(){
                    utils.post('/user/register.json', {
                        email: self.email, password: self.password, nickname: self.nickname
                    }, function (rs) {
                        toastr.success('注册成功!');
                        location.href = x.ctx + '/login';
                    });
                }).catch(function(){
                })
            }
        }
    });
});