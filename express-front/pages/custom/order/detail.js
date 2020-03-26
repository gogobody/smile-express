const app = getApp()
const util = require('../../../utils/util.js');
const service = util.service;
const { $Message } = require('../../../dist/base/index');

Page({
  data: {

  },
  onLoad: function (options) {
    console.log(options)
    this.setData({
      orderId: options.orderId
    })
    this.getOrder({
      "orderId": options.orderId
    })
  },
  onShow: function () {

  },
  getOrder: function (opt) {
    var that = this;
    service({
      url: '/order/find',
      data: opt,
      method: 'GET'
    }, res => {
      if (res.responseCode === 'SC0000') {
        console.log("detail",res.payload)
        that.setData({
          order: res.payload
        })
      } else if (res.responseCode === 'ER0001') {
        wx.showToast({
          title: res.responseMessage,
          icon: "none"
        });
      }
    }, error => {
      console.log(error)
    })
  },
  makePhoneCall: function (e) {
    var phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({
      phoneNumber: phone
    })
  },
  ordPay(e) {
    var id = e.currentTarget.dataset.id,
      that = this,
      status = this.data.status;

    if (id) {
      service({
        url: '/order/toPay',
        data: { orderId: id },
        method: 'POST'
      }, res => {
        if (res.responseCode === 'SC0000') {
          $Message({
            content: '支付成功',
            type: 'success'
          });
          this.getOrder({
            "orderId": id
          })
        } else {
          $Message({
            content: res.responseMessage,
            type: 'error'
          });
        }
      })
    } else {
      $Message({
        content: '并无此订单号',
        type: 'error'
      });
    }

  },
  reminderOrd: function () {
    var id = this.data.id;
    service({
      url: '/rest/wx/apis/ord/reminder',
      data: {
        id: id
      },
      method: 'GET',
      hideLoading: true
    }, res => {
      wx.showToast({
        title: res.message,
        duration: 3000,
        icon: "none",
        mask: true
      })
    }, err => {
      wx.showToast({
        title: err,
        icon: "none",
        duration: 3000,
        mask: true
      })
    })
  }
})