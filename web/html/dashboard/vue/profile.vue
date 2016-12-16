<template>
<div class="db-profile form">
    <div class="item">
        <div class="col-sm-2" style="line-height: 100px">头像</div>
        <div class="col-sm-10">            
                <div class="user-logo">
                    <img v-if='user.avatar' v-bind:src='user.avatar' alt="">              
                    <img src="../../assets/img/defaultlogo.jpg" v-else>
                    <div class="logo-edit" title="修改头像"><i class="iconfont icon-edit3"></i></div>
                    <input id="imagefile" type="file" v-on:change="uploadImage">
                </div>
        </div>
    </div>
    <div class="item">
        <div class="col-sm-2 full-text">姓名</div>
        <div class="col-sm-4"><input type="text" v-on:keyup="modify=true" v-model="user.nickname" value="{{user.nickname}}" class="text" placeholder="请输入姓名"></div>
    </div>

    <div class="item">
        <div class="col-sm-2">邮箱</div>
        <div class="col-sm-6">{{user.email}}</div>
    </div>
    <div class="item">
        <div class="col-sm-2">注册时间</div>
        <div class="col-sm-6">{{user.createtime}}</div>
    </div>

    <div class="item">
        <div class="col-sm-2 label"></div>
        <div class="col-sm-6"><input type="button" class="btn btn-primary" v-on:click="ok" v-bind:disabled="!modify" value="确认"></div>
    </div>
</div>

</template>
<script>
    import utils from '../../src/utils.js';
    export default{
        data:function(){
            return {
                user:utils.user(),
                modify:false                
            }
        },
        methods:{
            uploadImage: function(e) {          //图片上传前预览
                var self=this;
                if (e.target.files.length === 0) { return; }  //未选取图片时防止报错              
                let file = e.target.files[0];       //获取用户选取的图片                
                if(!/image\/\w+/.test(file.type)){  //图片类型筛选 
                    toastr.error('请上传图片！');
                    return false; 
                }                         
                var fd = new FormData();
                fd.append('avatar', file);
                utils.fileloader('/user/avatar.json',fd, 
                    function(rs){
                        self.user.avatar=rs.data.avatar;
                        utils.user(self.user);
                    });
            },
            ok:function(){
                var self=this;
                utils.post('/user/'+this.user.id+".json",{nickname:this.user.nickname},function(){
                    toastr.success('修改成功');
                    utils.user(self.user);
                });
            }
        }
    }
</script>