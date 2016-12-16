import Vue from 'vue';
import utils from '../../src/utils';

var vm = new Vue({
    el: '#history',
    data: {
        logs: [],          
        page:1,
        limit:7,
        id: null,
        noData:false,
        //newLogs:[],        //获取的新页面的logs
        loading:false,     //显示加载中动画
        bottom:false       //到达底部，么有更多logs了
    },
    created:function(){
            this.id = utils.getQueryParams(location.search).projectId;
            this.load();
    },
    watch: {
        "limit": function(){            
            $("html,body").animate({scrollTop:0},200);    //切换limit时scrollbar返回顶部
            this.load();
            return false;
        }     
    },
    methods:{
        reload: function(){
            location.reload();                            //点击刷新重载页面
        },
        load:function(){
            let self = this;
            //如果无数据了或者正在加载中就不加载
            if(self.noData || self.loading){
                return;
            }
            self.loading = true;
            utils.get("/projectlog.json", {projectId:self.id, limit:self.limit, page:self.page}, function(rs){
                self.page++;
                if(rs.data.logs.length == 0){
                    self.noData = true;
                }
                rs.data.logs.forEach(function(d){
                    d.createTime = showDate(d.createTime);   //设置日期显示格式
                    self.logs.push(d);
                });
            },function(){
                self.loading = false;
            });
            $(document).off('scroll').on('scroll',function(){
                if($(document).height() - $(window).scrollTop() <=$(window).height()){
                    self.load();
                }
            });           //绑定loadmore事件
        }
    }    
});

function showDate(createTime){
    let today = new Date();
    let thisYear = today.getFullYear();     //获取当前年份
    let thisDate = today.getDate();         //获取当前日期
    let thatYear = createTime.slice(0,4);   //判断是否是今年的logs       
    if (thatYear==thisYear){
        let thatDate = createTime.slice(5,10);
        if (thatDate == thisDate) {
          createTime = createTime.slice(11,16);  //今日记录只显示时间
        } else {
          createTime = createTime.slice(5,16);   //今年记录显示日期及时间
        }
    } else{
        createTime = createTime.slice(0,10);     //往年记录显示年份及日期
    }
    return createTime;
  }

