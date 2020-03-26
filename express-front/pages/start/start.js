const app = getApp();
const { $Toast } = require('../../dist/base/index');

Page({
  data: {
    angle: 0,
  },

  onLoad: function (options) {
    let that = this;
    wx.setNavigationBarTitle({
      title: '笑递',
    })
  },
  onShow: function () {
    var that = this;
    wx.onAccelerometerChange(function (res) {
      var angle = -(res.x * 30).toFixed(1);
      if (angle > 14) { angle = 14; }
      else if (angle < -14) { angle = -14; }
      if (that.data.angle !== angle) {
        that.setData({
          angle: angle
        });
      }
    });
  },
  goIndex: function() {
    // check userinfo
    
    let userInfo = wx.getStorageSync('userInfo');
    if (!userInfo) {
      wx.navigateTo({
        url: '/pages/auth/auth',
      })
    }else {
      //2、调用获取用户信息接口
      wx.login({
        success: function (res) {
          console.log(res)
          //3.请求自己的服务器，解密用户信息 获取unionId等加密信息
          if (res.errMsg == "login:ok" && res.code != '') {
            wx.showLoading({
              title: '正在拼命请求中...',
              mask: true
            })

            wx.request({
              url: app.globalData.baseUrl + '/login/loginByCode',//自己的服务接口地址
              method: 'POST',
              header: {
                "Content-Type": "applciation/json"
              },
              dataType: 'json',
              data: {
                code: res.code
              },
              success: function (data) {
                //4.解密成功后 获取自己服务器返回的结果
                wx.hideLoading();
                if (data.data.status == 0) {
                  console.log(data.data);
                  wx.setStorageSync('token', data.data.headers.token);
                  wx.setStorageSync('userInfo', Object.assign(wx.getStorageSync("userInfo"),data.data.payload));
                  console.log("跳转")
                  wx.switchTab({
                    url: '/pages/index/index',
                  })
                } else {
                  $Toast({
                    content: '请先绑定相关账号',
                    type: 'error'
                  });
                  setTimeout(()=> {
                    wx.navigateTo({
                      url: '/pages/auth/auth',
                    })
                  }, 1000)
                  // this.setData({
                  //   errorMsg: data.data.responseMessage
                  // })
                }
              },
              fail: function () {
                wx.hideLoading();
                $Toast({
                  content: '系统错误',
                  type: 'success'
                });
              }
            })
          }
          // wx.navigateTo({
          //   url: '/pages/login/login',
          // })
        }, 
        fail: function () {
          console.log('获取用户信息失败')
        }
      })
    }
  }
})