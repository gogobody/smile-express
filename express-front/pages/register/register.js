const app = getApp();
import { getUserAndBind } from '../../utils/api.js'
const { $Toast } = require('../../dist/base/index');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    position: 'left',
    checked: false,
    disabled: false,
    username: '',
    password: '',
    phone:'',
    errorMsg: ''
  },
  handleFruitChange({ detail = {} }) {
    console.log(detail);
    this.setData({
      current: detail.value
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  bindUser: function () {
    console.log(this.data.username, this.data.password);
    if (this.data.username != '' && this.data.password != '' && this.data.phone!='') {
      this.setData({
        errorMsg: ''
      })
      let username = this.data.username;
      let password = this.data.password;
      let phone = this.data.phone;
      let role = this.data.current;
      //2、调用获取用户信息接口
      wx.login({
        success: function (res) {
          console.log(res)
          //3.请求自己的服务器，解密用户信息 获取unionId等加密信息
          console.log(username, "ffff");
          if (res.errMsg == "login:ok" && res.code != '') {
            wx.showLoading({
              title: '请耐心等待',
              mask: true
            })
            wx.request({
              url: app.globalData.baseUrl + '/login/registerUser',//自己的服务接口地址
              method: 'POST',
              header: {
                "Content-Type": "applciation/json"
              },
              dataType: 'json',
              data: {
                username: username, 
                password: password,
                phone: phone
              },
              success: function (data) {
                wx.hideLoading();
                //4.解密成功后 获取自己服务器返回的结果
                if (data.data.status == 0) {
                  $Toast({
                    content: '注册成功',
                    type: 'success'
                  });
                  setTimeout(() => {
                    wx.navigateBack({
                      
                    })
                  }, 1500)
                } else {
                  $Toast({
                    content: data.data.responseMessage,
                    type: 'error'
                  });

                  // this.setData({
                  //   errorMsg: data.data.responseMessage
                  // })
                }
              },
              fail: function () {
                wx.hideLoading();
                this.setData({
                  errorMsg: '系统错误'
                })
              }
            })
          }
        },
        fail: function () {
          console.log('获取用户信息失败')
        }
      })
    } else {
      this.setData({
        errorMsg: '用户名或密码或手机不能为空'
      })
    }
  },
  setName({ detail = {} }) {
    console.log(detail.detail)

    this.setData({
      username: detail.detail.value
    })
  },
  setPwd({ detail = {} }) {
    this.setData({
      password: detail.detail.value
    })
  },
  setPhone({ detail = {} }) {
    this.setData({
      phone: detail.detail.value
    })
  },

})