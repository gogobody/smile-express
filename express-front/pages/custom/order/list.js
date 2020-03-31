const app = getApp()
const util = require('../../../utils/util.js');
const service = util.service;
const { $Message } = require('../../../dist/base/index');
Page({
  data: {
    currentIndex: "",
    delivering_List: null,
    isEmpty: false,
    pageIndex: 1,
    hasNext: true,
    status: "",
    wxInfo: wx.getStorageSync('userInfo'), // 获取微信的个人数据
    text:"\n"
  },
  onLoad: function () {

  },
  getlist: function (opt, callback) {
    var that = this
    service({
      url: '/order/list',
      data: {
        status: opt.status,
        pageSize: opt.pageSize || 5,
        pageNo: opt.page || 1
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
        
        if (res.payload.list.length) {
          let thisTime = null;
          let time = null;
          let nowTime = (new Date()).getTime();

          for (var i = 0; i < res.payload.list.length; i++) {
            thisTime = res.payload.list[i].getTime;
            console.log(thisTime)
            //thisTime = thisTime.replace(/-/g, '/');
            time = new Date(thisTime);
            time = time.getTime();
            console.log(time);
            if (time - nowTime < 0) {
              res.payload.list[i].remain_time = -parseInt((nowTime - time) / 1000 / 60); // 超时多少分钟
            } else {
              res.payload.list[i].remain_time = parseInt((time - nowTime) / 1000 / 60); // 剩余多少分钟
            }
          }
          if (res.payload.list.length < 5) {
            res.hasNext = false;
          }
          console.log(res)
        } else {
          res.hasNext = false;
        }
      }
      

      typeof callback == "function" && callback(res)
    });
  },
  switchTab: function (e) {
    let index = e.currentTarget.dataset.index,
      that = this,
      status = e.currentTarget.dataset.status || null,
      isEmpty = false;

    // if (status == 3) { // 待送货，这是配送员或是管理员才可以有的
    //   let userInfo = wx.getStorageSync("userInfo");
    //   if (userInfo.isSuper || userInfo.taker) {
    //     return;
    //   } else {
    //     $Message({
    //       content: '请先成为配送员',
    //       type: 'error'
    //     });
    //     return;
    //   }
    // }

    this.getlist({ status: status }, res => {
      if (res.payload.list.length == 0) {
        isEmpty = true;
      }

      that.setData({
        isEmpty: isEmpty,
        status: status,
        all_list: res.payload.list,
        hasNext: true,
        pageIndex: 1
      })
    });
    this.setData({
      currentIndex: index
    })

    app.globalData.getOrdList = {
      tab: index,
      status: that.data.status
    }
  },
  onShow: function () {
    var status = 0, currentIndex = 1, that = this;
    this.setData({
      hasNext: true,
      pageIndex: 1
    })
    if (app.globalData.getOrdList) {
      status = app.globalData.getOrdList.status;
      currentIndex = app.globalData.getOrdList.tab
    }
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          pageHeight: res.windowHeight
        })
      },
    })
    var that = this;
    this.getlist({
      status: status
    }, res => {
      if (!res.payload) {

        return false;
      }
      console.log("currentIndex",currentIndex)
      that.setData({
        currentIndex: currentIndex,
        all_list: res.payload.list
      })
      console.log(this.data.all_list);
    })

  },
  onHide: function () {
    var that = this;
    app.globalData.getOrdList = {
      status: that.data.status,
      tab: that.data.currentIndex
    }
  },
  addScore:function(e){
    var data = e.currentTarget.dataset.info;
    wx.navigateTo({
      url: '../score/add?orderId='+data.orderId
    })
  },
  confirmOrder: function(e) {
    console.log(e);
    let orderId = e.currentTarget.dataset.status || null;
    service({
      url: '/order/confirmOrder',
      data: {
        orderId: orderId
      },
      method: 'POST'
    }, res => {
      console.log(res);
      if (res.responseCode == "SC0000") {
        $Message({
          content: '确认收货成功',
          type: 'success'
        });
        this.setData({
           status: 3,
           hasNext: true,
           pageIndex: 1
        })
        this.onShow();
      } else if (res.status == 301 || res.status == 302){
        wx.showToast({
          title: res.responseMessage,
          icon: 'none'
        });
        setTimeout(()=> {
          wx.redirectTo({
            url: '../../start/start',
          })
        }, 1500)
      } else {
        wx.showToast({
          title: res.responseMessage,
          icon: 'none'
        });
      }
    })
  },
  loadMore: function () {
    var pageIndex = this.data.pageIndex,
      status = this.data.status,
      hasNext = this.data.hasNext;
      console.log(hasNext);
    pageIndex++;
    var that = this;
    this.setData({
      pageIndex: pageIndex
    })
    var orderList = this.data.all_list;
    if (!hasNext) {
      return false;
    }
    this.getlist({
      status: status,
      page: pageIndex
    }, res => {
      var list = orderList.concat(res.payload.list)
      that.setData({
        all_list: list,
        hasNext: res.hasNext
      })
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
  cancelOrd:function(e){
    var id = e.currentTarget.dataset.id;
    if (id) {
      service({
        url: '/order/cancel',
        data: { orderId: id },
        method: 'POST'
      }, res => {
        if (res.responseCode === 'SC0000') {
          $Message({
            content: '取消成功',
            type: 'success'
          });
          var that = this,currentIndex=1;
          this.getlist({
            status: 0
          }, res => {
            if (!res.payload) {
      
              return false;
            }
            console.log("currentIndex",currentIndex)
            that.setData({
              currentIndex: currentIndex,
              all_list: res.payload.list
            })
            console.log(this.data.all_list);
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
  reminderOrd: function (e) {
    var id = e.currentTarget.dataset.id;
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