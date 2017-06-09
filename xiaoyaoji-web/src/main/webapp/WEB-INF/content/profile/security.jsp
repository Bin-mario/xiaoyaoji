<%--
  User: zhoujingjie
  Date: 17/4/8
  Time: 13:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="header.jsp"/>
<div class="project-info mc">
    <jsp:include page="left.jsp"/>
    <div class="project-info-content" id="content">
        <div class="db-security form">
            <h3 class="title">安全设置</h3><br/><br/>
            <div class="item">
                <div class="col-sm-2">更换邮箱</div>
                <div class="col-sm-6">{{user.email}}</div>
                <div class="col-sm-2">
                    <button type="button" class="uk-button uk-button-primary" uk-toggle="target:#emailModal" >修改</button>
                </div>
            </div>
            <div class="item">
                <div class="col-sm-2">更换密码</div>
                <div class="col-sm-6">建议您3个月修改一次</div>
                <div class="col-sm-2">
                    <button type="button" class="uk-button uk-button-primary" uk-toggle="target:#passwordModal">修改</button>
                </div>
            </div>

        </div>

        <div id="emailModal" v-cloak uk-modal>
            <div class="uk-modal-dialog">
                <div class="uk-modal-header">
                    <h2 class="uk-modal-title">修改邮箱</h2>
                </div>

                <div class="uk-modal-body form">
                    <div class="item">
                        <div class="col-sm-12">
                            <input type="text" name="email" v-model="email" tabindex="1" maxlength="45"
                                   v-validate="'required|email'" class="k1 text" placeholder="请输入新的邮箱"/>
                            <p class="tip" >{{ errors.first('email') }}</p>
                        </div>
                    </div>

                    <div class="item">
                        <div class="col-sm-8">
                            <input type="text" class="text k1" tabindex="2" name="captcha" v-model="captcha"
                                   v-validate="'required'" placeholder="请输入验证码">
                            <p v-cloak class="tip" >{{ errors.first('captcha') }}</p>
                        </div>
                        <div class="col-sm-4">

                            <button class="uk-button uk-button-primary" style="padding: 4px 20px!important" v-on:click="sendCaptcha">{{time>0?(time+'s'):'获取验证码'}}
                            </button>
                        </div>
                    </div>
                </div>

                <div class="uk-modal-footer uk-text-right">
                    <button class="uk-button uk-button-default uk-modal-close" type="button" v-on:click="emailModal=false">取消</button>
                    <button class="uk-button uk-button-primary" type="button" v-bind:disabled="errors.has('email') ||errors.has('captcha')" v-on:click="ok" >确定</button>
                </div>

            </div>
        </div>


        <div id="passwordModal" v-cloak uk-modal>
            <div class="uk-modal-dialog">
                <div class="uk-modal-header">
                    <h2 class="uk-modal-title">修改密码</h2>
                </div>
                <div class="uk-modal-body form">
                    <div class="item">
                        <div class="col-sm-12">
                            <input type="password" name="password" v-model="password" tabindex="1" maxlength="45"
                                   v-validate="'required'"
                                   class="k1 text" placeholder="请输入新的密码"/>
                            <p class="tip">{{errors.first('password')}}</p>
                        </div>
                    </div>
                    <div class="item">
                        <div class="col-sm-12">
                            <input type="password" name="repassword" v-model="repassword" tabindex="1" maxlength="45"
                                   v-validate="'required'"
                                   class="k1 text" placeholder="请确认密码"/>
                            <p v-cloak class="tip">请再次输入密码</p>
                        </div>
                    </div>
                </div>

                <div class="uk-modal-footer uk-text-right">
                    <button class="uk-button uk-button-default uk-modal-close" type="button" v-on:click="emailModal=false">取消</button>
                    <button class="uk-button uk-button-primary" type="button"  v-bind:disabled="errors.has('password') || errors.has('repassword')" v-on:click="passwordOk" >确定</button>
                </div>

            </div>
        </div>

    </div>
</div>
<jsp:include page="/WEB-INF/includes/js.jsp"/>
<script>
    require(['vue', 'utils','vueEx'], function (Vue, utils) {
        new Vue({
            el: '#content',
            data:  {
                user: utils.toJSON('${user}'),
                emailModal: false,
                passwordModal: false,
                password: '',
                repassword: '',
                email: '',
                captcha: '',
                time: 0
            },
            validators: {
                newEmail: {
                    message: '新邮箱与当前邮箱一样',
                    check: function (value) {
                        return value != this.vm.user.email;
                    }
                }
            },
            methods: {
                sendCaptcha: function () {
                    var self = this;
                    this.$validator.validateAll({email:this.email}).then(function(){
                        utils.post('/user/email/captcha', {email: self.email}, function (rs) {
                            self.time = 30;
                            var interval = window.setInterval(function () {
                                if (self.time > 0) {
                                    self.time--;
                                } else {
                                    window.clearInterval(interval);
                                }
                            }, 1000);
                        });
                    }).catch(function(){
                    });

                    if (this.time > 0) {
                        return false;
                    }
                },
                ok: function () {
                    var self = this;
                    this.$validator.validateAll({email:this.email,captcha:this.captcha}).then(function(){
                        utils.post('/user/email/new', {email: self.email, code: self.captcha}, function (rs) {
                            toastr.success('修改成功');
                            self.emailModal = false;
                            self.user.email = self.email;
                            UIkit.modal('#emailModal').hide()
                        });
                    }).catch(function(){
                    });


                },
                passwordOk: function () {
                    if (this.password != this.repassword) {
                        toastr.error('两次密码不一样');
                        return false;
                    }
                    var self = this;
                    this.$validator.validateAll({password:this.password,repassword:this.repassword}).then(function(){
                        utils.post('/user/password', {password: self.password}, function (rs) {
                            toastr.success('修改成功');
                            self.passwordModal = false;
                            UIkit.modal('#passwordModal').hide()
                        });
                    }).catch(function(){
                        toastr.error('请输入正确的内容');
                    });
                }
            }
        })
    });
</script>