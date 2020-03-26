// pages/custom/center/center.js
//获取应用实例
const app = getApp();
const { $Toast } = require('../../../dist/base/index');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    wxInfo: wx.getStorageSync('userInfo'), // 获取微信的个人数据
    userInfo: ''
  },

  
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
       console.log("djjdj")
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    console.log("onSHow");
    var that = this;
    wx.request({
      url: app.globalData.baseUrl + '/login/getUserInfo',//自己的服务接口地址
      method: 'POST',
      header: {
        "Content-Type": "applciation/json",
        "Authorization": wx.getStorageSync("token")
      },
      dataType: 'json',
      success: function (data) {
        console.log(data.data);
        if (data.data.responseCode == "SC0000") {
          that.setData({
            userInfo: data.data.payload
          });
          
        } else {
          $Toast({
            content: '获取用户信息失败！',
            type: 'success'
          });
        }
          
      },
      fail: function () {
        $Toast({
          content: '网络异常',
          type: 'error'
        });
      }
    })
  },
  switchTab: function(e){
    let status = e.currentTarget.dataset.status;
    if (status == 'getOrder') { // 抢单
       if(this.data.userInfo.taker || this.data.userInfo.isVip) {
         wx.navigateTo({
           url: '../../custom/taker/list',
         }) 
       } else {
         $Toast({
           content: '请先成为配送员',
           type: 'error'
         });
       }
      
    } else if (status == 'getReceived') {
      if (this.data.userInfo.taker || this.data.userInfo.isVip) {
        wx.navigateTo({
          url: '../../custom/credit/list',
        })
      } else {
        $Toast({
          content: '请先成为配送员',
          type: 'error'
        });
      }
    }else if(status == 'finish'){
      wx.navigateTo({
        url: '../score/list',
      })
    }
  },
  examIn: function() {
    wx.navigateTo({
      url: '../../admin/exam/exam',
    })
  },
  entryAdmin: function() {
    wx.navigateTo({
      url: '../../admin/entry/entry',
    })
  }
})