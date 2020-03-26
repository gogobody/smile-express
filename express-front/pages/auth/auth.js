const app = getApp()
Page({
  data: {},
  bindGetUserInfo: function (e) {
    

    if (!e.detail.userInfo) {
      wx.showToast({
        title: '您拒绝了授权',
        image: '/images/help/kulian.png'
      });
      return false;
    }
    // 将用户信息缓存
    wx.setStorageSync('userInfo', e.detail.userInfo);
    // 不起作用
    app.globalData.userInfo = e.detail.userInfo;
   
    wx.navigateTo({
      url: '../login/login',
    })
    // 返回上一页
    //wx.navigateBack({})
  }
})