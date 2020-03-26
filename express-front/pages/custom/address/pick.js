const app = getApp();
const util = require('../../../utils/util.js');
const service = util.service;
const { $Toast } = require('../../../dist/base/index');
Page({
  data: {
    isEmpty: false,
    input: '',
    react: false,
    title: null,
    orderType: null,
    addressType: null,
    sortMode: 'last_use_time',
    currentPage: 1,
    currentAddress:null
  },
  clearInput: function () {
    this.setData({
      input: "",
      isEmpty: false
    })
    this.getlist()
  },
  searchInput: function (e) {
    let value = e.detail.value;
    if (value) {
      this.setData({
        isEmpty: true,
        input: value
      })
      this.getlist()
    } else {
      this.setData({
        isEmpty: false,
        input: ''
      })
      this.getlist()
    }
  },
  onLoad: function (options) {
    console.log(options);
    let that = this;
    //选择地址
    if (options.orderType !== undefined && options.orderType !== "null") {
      wx.setNavigationBarTitle({
        title: options.title || "地址管理"
      });
  
      that.setData({
        orderType: options.orderType,
        addressType: options.addressType,
        title: options.title,
        react: true,
      })
      console.log("react"+this.data.react)

    } else {
      wx.setNavigationBarTitle({
        title: "地址管理"
      });
    }
    console.log("执行中。。。。。");
    //this.getlist()
  },
  onShow: function (option) {
    console.log(option);
    this.getlist()
  },
  getlist: function (opt) {
    var that = this,
      key = that.data.input;
      // data = that.data,
      // orderType = data.orderType,
      // addressType = data.addressType,
      // latIng = app.globalData.latIng,
      // master = data.master,
      // sortMode = data.sortMode,
      // currentPage = data.currentPage;

    service({
      url: '/address/list',
      data: {
        areaName: key
      }
    }, res => {
      console.log(res);
      if(res.status == 302) {
         wx.redirectTo({
           url: '../../start/start',
         })
         return ;
      } else if(res.status == 301) {
        wx.showToast({
          title: res.responseMessage,
          icon: 'none'
        })
        setTimeout(()=> {
          wx.redirectTo({
            url: '../../start/start',
          })
        }, 1500)
        return;
      } else if(res.status == 0){
        if (!res.payload.length) {
          that.setData({
            listIsNull: true,
            addressList: null
          })
          console.log(that.data.addressList)
          return false;
        }
        that.setData({
          addressList: res.payload,
          listIsNull: false
        });

        for (let i = 0; i < res.payload.length; i++) {
          if (res.payload[i].latitude == app.globalData.latIng.latitude && res.payload[i].longitude == app.globalData.latIng.longitude) {
            that.setData({
              currentAddress: res.payload[i]
            })
          }
        }
        console.log("currentAddress:" + this.currentAddress);
      } else {
        wx.showToast({
          title: '其他错误',
          icon: 'none'
        })
      }
      
    });
  },
  chooseAddress: function (e) {
    console.log("chooseAddress");
    console.log(e);
    let data = e.currentTarget.dataset.info
    if (!this.data.react) {
      return false;
    }
    app.globalData.indexTab = this.data.orderType;
    app.globalData.addressType = this.data.addressType;
    app.globalData.addressId = data.id;
    wx.switchTab({
      url: '../../index/index'
    })
  },
  deleteAddress: function (e) {
    let that = this;
    console.log(e);
    wx.showModal({
      content: '地址删除后将无法恢复，您确定要删除吗？',
      success: res => {
        if (res.cancel) {
          return false;
        }
        service({
          url: '/address/delete',
          method: 'POST',
          data: {
            id: e.currentTarget.dataset.id
          }
        }, res => {
          service({
            url: '/address/list'
          }, res => {
            if (!res.payload.length) {
              that.setData({
                listIsNull: true,
                addressList: null
              })
              return false;
            }
            that.setData({
              addressList: res.payload
            })
          });
        })
      }
    })
  },
  editAddress: function (e) {
    var info = e.currentTarget.dataset.info,
      data = this.data;
      console.log(info);
    wx.redirectTo({
      url: 'add?id=' + info.id + '&lat=' + info.latitude + '&lng=' + info.longitude + '&address=' + info.address + '&name=' + info.name + '&phone=' + info.phone + '&areaName=' + info.areaName + '&adcode=' + info.adcode + '&title=' + data.title
    })
  }
})