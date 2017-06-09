define(['vue', 'veeValidate'], function (Vue, VeeValidate) {
    var messages = (function(){
        var msg =  {
            username:'用户名',
            email:'邮箱',
            nickname:'昵称',
            password:'密码',
            moduleName:'模块名称',
            folderName:'文件夹名称',
            projectName:'项目名称',
            captcha:'验证码'
        };
        return {
            getMessage:function(name){
                var value= msg[name];
                if(!value){
                    value = name;
                }
                return value;
            }
        }
    })();




    Vue.use(VeeValidate,{
        enableAutoClasses:true,
        locale:'cn',
        events: 'input|blur',
        dictionary:{
            cn:{
                messages:{
                    required:function(name){
                        return '请输入'+messages.getMessage(name);
                    },
                    email:function(){
                        return '邮箱输入错误，正确格式为：abc@domain.com';
                    }
                }
            }
        }
    });


    Vue.filter('html', function (value) {
        if (value) {
            return value.replace(/\n/g, '<br/>')
        }
        return '';
    });
    return {}
});
