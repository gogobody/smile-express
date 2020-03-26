
const app = getApp()
const util = require('../../../utils/util.js');
const service = util.service;
var page = 1;
var page_size = 5;
var sort = "last";
var is_easy = 0;
var lange_id = 0;
var pos_id = 0;
var unlearn = 0;


// 请求数据
var loadMore = function (that) {
  that.setData({
    hidden: false
  });
  service({
    url: '/login/getIncomeList',
    data: {
      pageSize: page_size || 5,
      pageNo: page || 1
    },
    method: 'GET'
  }, res => {
    console.log(res.payload);
    if (res.responseCode === 'ER0001') {
      wx.showToast({
        title: res.responseMessage,
        icon: "none"
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '../../start/start'
        })
      }, 1500)
    } else {
      if (res.payload.list.length>0) {
        var list = that.data.list;
        for (var i = 0; i < res.payload.list.length; i++) {
          list.push(res.payload.list[i]);
        }
        that.setData({
          list: list
        });
        
      } else {

      }
      page++;
      that.setData({
        hidden: true
      })
    
    }
  });

  // wx.request({
  //   url: url,
  //   data: {
  //     page: page,
  //     page_size: page_size,
  //     sort: sort,
  //     is_easy: is_easy,
  //     lange_id: lange_id,
  //     pos_id: pos_id,
  //     unlearn: unlearn
  //   },
  //   success: function (res) {
  //     //console.info(that.data.list);
  //     var list = that.data.list;
  //     for (var i = 0; i < res.data.list.length; i++) {
  //       list.push(res.data.list[i]);
  //     }
  //     that.setData({
  //       list: list
  //     });
  //     page++;
  //     that.setData({
  //       hidden: true
  //     });
  //   }
  // });
}
Page({
  data: {
    hidden: true,
    list: [],
    scrollTop: 0,
    scrollHeight: 0,
    amount: 0.00
  },
  onLoad: function () {
    //   这里要注意，微信的scroll-view必须要设置高度才能监听滚动事件，所以，需要在页面的onLoad事件中给scroll-view的高度赋值
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          scrollHeight: res.windowHeight
        });
      }
    });
    service({
      url: '/login/getIncomeAmount',
      data: {
        
      },
      method: 'GET'
    }, res => {
       that.setData({
         amount: res.payload
       })
    });
    loadMore(that);
  },
  //页面滑动到底部
  bindDownLoad: function () {
    var that = this;
    loadMore(that);
    console.log("lower");
  },
  scroll: function (event) {
    //该方法绑定了页面滚动时的事件，我这里记录了当前的position.y的值,为了请求数据之后把页面定位到这里来。
    this.setData({
      scrollTop: event.detail.scrollTop
    });
  },
  topLoad: function (event) {
    //   该方法绑定了页面滑动到顶部的事件，然后做上拉刷新
    page = 1;
    this.setData({
      list: [],
      scrollTop: 0
    });
    loadMore(this);
    console.log("lower");
  }
})