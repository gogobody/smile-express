const app = getApp();
const map = require('../../../utils/map.js');
const util = require('../../../utils/util.js');
const { $Toast } = require('../../../dist/base/index');
const QQmap = map.QQmap;

const service = util.service;
Page({
  data: {
    url: 'pick',
    url:''
  },
  onLoad: function (options) {
    console.log(options);
    // 经纬度存在
    if (options.lat && options.lng) {
      this.setData({
        latitude: options.lat,
        longitude: options.lng,
        adcode: options.adcode
      });
      var latIng = {
        latitude: options.lat,
        longitude: options.lng
      }
      this.reverseGeocoder(latIng)
    } else {
      this.setData({
        isNull: true
      });
    }
    // 编辑
    if (options.id) {
      this.setData({
        isEdit: true,
        id: options.id,
        areaName: options.areaName || null,
        address: options.address,
        name: options.name,
        phone: options.phone,
        adcode: options.adcode
      });
      wx.setNavigationBarTitle({
        title: '编辑地址'
      })
    }
    if (options.orderType !== undefined) {
      if (options.orderType > 0 && options.addressType > 0 && options.title) {
        app.globalData.indexTab = options.orderType;
        app.globalData.addressType = options.addressType;
        this.setData({
          returnIndex: true
        })
      } else {
        this.setData({
          url: 'pick?orderType=' + options.orderType + '&addressType=' + options.addressType + '&title=' + options.title + '&master=' + options.master
        })
      }
    }
    if (options.master == 1) {
      this.setData({
        master: options.master
      })
    }

  },
  onShow: function () {

    if (app.globalData.scanStr) {
      this.setData({
        address: app.globalData.scanStr
      })
      app.globalData.scanStr = undefined
    }
  },
  reverseGeocoder: function (latIng) {
    let that = this;
    QQmap.reverseGeocoder({
      location: {
        latitude: latIng.latitude,
        longitude: latIng.longitude
      },
      success: function (res) {
        var data = res.result
        that.setData({
          areaName: data.formatted_addresses.recommend
        });

      }
    })
  },
  getAddr:function (e) {
    if (e.detail.detail.value) {
      this.setData({
        addr: e.detail.detail.value
      })
    }
  },
  getReason: function (e) {
    console.log(e.detail.detail.value)
    if (e.detail.detail.value) {
      this.setData({
        reason: e.detail.detail.value
      })
    }
  },
  getPhone: function (e) {
    if (e.detail.detail.value) {
      this.setData({
        phone: e.detail.detail.value
      })
    }
  },
  formSubmit: function () {
    if(!this.data.reason||!this.data.addr||!this.data.phone||!this.data.url){
      wx.showToast({
        title: '请完成步骤',
      })
      return
    }
    console.log(this.data);
    var that = this;
    var _data = {
      reason: this.data.reason,
      addr: this.data.addr,
      phone: this.data.phone,
      photo:this.data.url
    }
    service({
      url: '/adm/add',
      data: _data,
      method: 'POST'
    }, (data) => {
      console.log(data);
      if (data.responseCode == "SC0000") {
        $Toast({
          content: '申请成功！',
          type: 'success'
        });
        setTimeout(() => {
          wx.navigateBack({
          })
        }, 1500)
      } else {
        $Toast({
          content: data.responseMessage,
          type: 'error'
        });
      }
    })
  },
  uploadimg:function(){
    var that = this
  
    wx.chooseImage({
      success: ret => {
        var filePath = ret.tempFilePaths[0];
        wx.showLoading({
          title: '正在上传'
        })
        wx.uploadFile({
          url: getApp().globalData.baseUrl+'/adm/upload',
          filePath: filePath,
          name: 'name',
          success: res => {
            console.log(res.data)
            var r = JSON.parse(res.data)
            if(r.status ==0){
              wx.showToast({
                title: '上传成功',
              })
              console.log('上传成功：', r);
              
              let url =app.globalData.baseuri+ r.payload.url
              that.setData({
                url:url
              })
             
            }else{
              wx.showToast({
                title: '上传失败'
              })
            }
            wx.hideLoading({
              complete: (res) => {},
            })
          }
        });
      }
    })
  }

})