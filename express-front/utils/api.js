function getUserAndBind(name, password, role) {
 
  //2、调用获取用户信息接口
  wx.getUserInfo({
    success: function (res) {
      console.log({ encryptedData: res.encryptedData, iv: res.iv, code: res.code })
      //3.请求自己的服务器，解密用户信息 获取unionId等加密信息
      wx.request({
        url: globalData.baseUrl+'/login/bindUser',//自己的服务接口地址
        method: 'POST',
        header: {
          "Content-Type": "applciation/json"
        },
        data: { username: name,password: password, role: role.id, code: code },
        success: function (data) {
          
          //4.解密成功后 获取自己服务器返回的结果
          if (data.data.status == 0) {
            var userInfo_ = data.data.userInfo;
            console.log(userInfo_)
          } else {
            console.log(data.data.responseMessage);
          }
        },
        fail: function () {
          console.log('系统错误')
        }
      })
    },
    fail: function () {
      console.log('获取用户信息失败')
    }
  })
}