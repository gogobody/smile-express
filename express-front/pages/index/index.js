//获取应用实例
const app = getApp()
const util = require('../../utils/util.js');
const GDmap = require('../../libs/amap-wx.js');
const { $Message } = require('../../dist/base/index');
const service = util.service;
Page({
  data: {
    currentIndex: 1,
    userInfo: {},
    hasUserInfo: false,
    markers: [],
    rule: "收费balabal",
    isSlide: true,
    visibleButton: false,
    heightLightStyle: "",
    slideRules: false,
    isBindPhone: true,
    pick: null,
    send: null,
    distance: 0,
    cost: 0,
    isToday: true,
    pay_show: false
  },
  onLoad: function () {
    let that = this;
    this.getTodayTime()
    this.setData({
      currentIndex: 1
    });

    that.setData({
        costConfig: {
          create_time: new Date(),
          end_time: new Date(),  // 结束时间
          marki_user_rate: 1.5,
          more_km: 10,
          more_km_price: 1,
          other_time_rate: 0, // 其他时间都设置为0
          start_km:0.5,  
          start_price: 1,  //开始的价格
          start_time: new Date(),  // 开始时间
          marki_user_rate: 2,
          update_time: new Date()
        }
      })

    this.formatTime();

  },
  onShow: function () {
    var that = this, pick = this.data.pick, send = this.data.send;
    var currentIndex = app.globalData.indexTab ? app.globalData.indexTab : 1,
      send = null,
      pick = null;
    // _distance = {},
    // hours = this.data.hours,
    // minutes = this.data.minutes;
    this.setData({
      currentIndex: currentIndex,
      date: new Date(),
      task_type: currentIndex == 1 ? 'help' : 'buy'
    })
    if (app.globalData.addressId) {
      this.getAddress()
    }
    console.log(app.globalData.addressType)


  },
  // 展开配送规则
  slideRules: function () {
    if (this.data.slideRules == true) {
      return false;
    }
    wx.pageScrollTo({
      scrollTop: '500px',
      duration: 300
    })
    this.setData({
      slideRules: true,
    })
  },
  getTodayTime: function () {
    var currentTime = new Date(),
      year = currentTime.getFullYear(),
      month = currentTime.getMonth() + 1,
      date = currentTime.getDate(),
      day = currentTime.getDay(),
      hours = currentTime.getHours(),
      minutes = currentTime.getMinutes(),
      earlyMinutes = 0,
      earlyHours = 0,
      _marr = [];
    // 取货时间 -minutes
    if (minutes % 5 == 0) {
      earlyMinutes += 5;
    } else {
      var n = 5 - (minutes % 5)
      earlyMinutes = minutes + n + 5

    }
    earlyMinutes = earlyMinutes >= 60 ? earlyMinutes - 60 : earlyMinutes;
    if (earlyMinutes >= 60) {
      earlyMinutes = earlyMinutes - 60;
      earlyHours = hours + 1;
    } else {
      earlyHours = hours
    }
    var _mlen = (60 - earlyMinutes) / 5;
    for (var i = 0; i < _mlen; i++) {
      _marr.push(earlyMinutes + (i * 5))
    }
    var _hlen = 23 - earlyHours
    var _harr = [];
    for (var h = 0; h <= _hlen; h++) {
      _harr.push(h + earlyHours)
    }
    if (hours <= 1) {
      _marr.slice(0, 7);
    }
    this.setData({
      order_date: {
        year: year,
        month: month,
        date: date
      },
      optionTime: [
        this.formatDay(day),
        _harr,
        _marr
      ]
    })
  },

  // 格式化时间
  formatTime: function () {
    // 全部可选时间 --hours
    var hours_optional = [],
      minutes_optional = [];
    for (var a = 0; a <= 23; a++) {
      if (a <= 1 || a >= 10) {
        hours_optional.push(a);
      }
    }
    // 全部可选时间 --minutes
    for (var k = 0; k <= 12; k++) {
      minutes_optional.push(k * 5);
    }
    this.setData({
      hours_optional: hours_optional,
      minutes_optional: minutes_optional
    })

  },
  // 格式化日期
  formatDay: function (day) {
    let today = "今天",
      tomorrow = "明天";
    switch (day) {
      case 1:
        today += "（周一）";
        tomorrow += "(周二)";
        break;
      case 2:
        today += "（周二）"
        tomorrow += "(周三)";
        break;
      case 3:
        today += "（周三）"
        tomorrow += "(周四)";
        break;
      case 4:
        today += "（周四）"
        tomorrow += "(周五)";
        break;
      case 5:
        today += "（周五）";
        tomorrow += "(周六)";
        break;
      case 6:
        today += "（周六）"
        tomorrow += "(周日)"
        break;
      case 7:
        today += "（周日）"
        tomorrow += "(周一)";
        break;
    }
    return [
      today,
      tomorrow
    ]
  },
  // 监听取货日期
  pickerChange: function (e) {
    console.log("pickerChange");
    var cho = e.detail,
      time = this.data.optionTime,
      marr = this.data.minutes_optional,
      isToday = this.data.isToday;
    var currentTime = new Date(),
      day = currentTime.getDay();
    // today
    if (cho.column === 0 && cho.value === 0) {

      this.setData({
        isToday: true
      })
      this.getTodayTime()
    }
    // tomorrow
    if (cho.column === 0 && cho.value === 1) {
      this.getTomorrowTime(true)
      this.setData({
        isToday: false
      })
    }
    // tomorrow night
    if (cho.column === 1 && !isToday) {
      var isMiddleNight = true;
      if (cho.value > 1) {
        isMiddleNight = false;
      }
      this.getTomorrowTime(isMiddleNight);
    }
    if (cho.column === 1 && isToday) {
      if (cho.value === 0) {
        this.getTodayTime();
      } else {
        time[2] = marr;
        this.setData({
          optionTime: [
            time[0],
            time[1],
            time[2]
          ]
        })
      }
    }
  },
  // 选择取货时间
  timeChange: function (e) {
    console.log("timeChange");
    var value = e.detail.value,
      optionTime = this.data.optionTime,
      order_date = this.data.order_date;

    value[2] = value[2] === null ? 0 : value[2]

    var hours = optionTime[1][value[1]] ? optionTime[1][value[1]] : optionTime[1][0],
      minutes = optionTime[2][value[2]] ? optionTime[2][value[2]] : optionTime[2][0],
      getTime = order_date.year + "-" + order_date.month + "-" + order_date.date + " " + hours + ":" + minutes + ":00";
    var get_time_day = optionTime[0][value[0]] ? optionTime[0][value[0]] : optionTime[0][0],
      get_time_hours = hours + ":" + minutes
   
    console.log(getTime);
    this.setData({
      get_time: get_time_day + get_time_hours,
      getTime: getTime
    })

    this.getCost()
  },
  getTomorrowTime: function (isNight) {
    var time = this.data.optionTime,
      marr = this.data.minutes_optional,
      order_date = this.data.order_date;
    order_date.date = order_date.date + 1;
    time[1] = this.data.hours_optional;

    if (isNight) {
      time[2] = marr.slice(0, 7)
    } else {
      time[2] = marr
    }
    this.setData({
      order_date: order_date,
      optionTime: time,
      isMiddleNight: true
    })

  },
  getNote: function (e) {
    // console.log(e);
    this.setData({
      note: e.detail.value
    })
  },


  // 展开form
  slideForm: function (e) {
    var index = e.currentTarget.dataset.index,
      task_type = index == 1 ? "help" : "buy";
    if (index == 1) {
      this.setData({
        send: null
      })
      //this.getRecentAddr('from')
    } else {
      this.setData({
        pick: null
      })
      //this.getRecentAddr('to')
    }
    this.setData({
      currentIndex: index,
      task_type: task_type
    });
  },

  // 计费
  getCost: function () {
    var config = this.data.costConfig,
      date = this.data.date,
      distance = this.data.distance,
      cost = 0;
    var now = new Date(),
      hours = now.getHours(),
      minutes = now.getMinutes()
    // 营业时间是否跨夜
    var night = config.start_time > config.end_time ? true : false;
    // 跨夜 同时大于开始结束时间or同时小于开始结束时间
    if (!night) {
      if ((hours > config.start_time && hours >= config.end_time) || (hours < config.start_time && hours < config.end_time)) {
        console.log("A")
        cost = this.non_business(distance)
      } else {
        console.log("B")
        cost = this.business(distance)
      }
    } else {
      //  大于结束或小于开始
      var valid1 = hours >= config.start_time || hours < config.end_time;
      var valid2 = hours == config.end_time && minutes == 0;
      console.log("valid1", valid1);
      console.log("valid2", valid2);
      if ((valid1 || valid2) && distance) {
        cost = this.business(distance)
      } else if (distance) {
        cost = this.non_business(distance)
      }
    }
    this.setData({
      cost: cost.toFixed(2),
      clock: hours + ":" + minutes + ":00"
    })
  },
  // 正常服务时间
  business: function (d) {
    var config = this.data.costConfig;
    var res = 0;
    if (d < config.start_km) {
      res = config.start_price;
    } else {
      res = config.start_price + ((d - config.start_km) / config.more_km) * config.more_km_price  
    }
    console.log(d);
    console.log(res);
    return res
  },
  // 非正常服务时间
  non_business: function (d) {
    var config = this.data.costConfig;
    var origin = this.business(d);
    origin = origin + origin * (config.other_time_rate / 100);
    return origin;
  },




  // 获取地址
  getAddress: function () {
    if (!app.globalData.indexTab) {
      return false;
    }
    var that = this,
      distance = {},
      send = that.data.send,
      pick = that.data.pick;
    //hours = that.data.hours,
    //minutes = that.data.minutes;
    service({
      url: '/address/find',
      data: {
        id: app.globalData.addressId,
      },
      method: 'POST'
    }, res => {
      // pick
      console.log(res);
      if (res.responseCode === "SC0000") {
        if (app.globalData.addressType == 1) {
          pick = res.payload;
          that.setData({
            pick: {
              latitude: res.payload.latitude,
              longitude: res.payload.longitude,
              address: res.payload.address,
              name: res.payload.name,
              phone: res.payload.phone,
              areaName: res.payload.areaName,
              adcode: res.payload.adcode
            }
          })
        } else if (app.globalData.addressType == 2) {
          send = res.payload;
          that.setData({
            send: {
              latitude: res.payload.latitude,
              longitude: res.payload.longitude,
              address: res.payload.address,
              name: res.payload.name,
              phone: res.payload.phone,
              areaName: res.payload.areaName,
              adcode: res.payload.adcode
            }
          })
        }
        
        console.log("send"+send);
        console.log( send);
        console.log("pick"+pick);
        console.log(pick);

        if (send && pick) {
          that.calculateDistance(send, pick, () => {
            var distance = that.data.distance;
            that.setData({
              distance: distance
            })
            that.getCost(distance)


          })
        }

      } else {
        wx.showToast({
          title: res.responseMessage,
          icon: "none"
        })
      }
      

    }, err => {
      wx.showToast({
        title: err,
        icon: "none"
      })
    })


  },
  // 计算距离
  calculateDistance: function (p, s, cb) {
    var that = this;
    var myAmapFun = new GDmap.AMapWX({ key: '1d005fe07d3303a9d10e56da8c7cae06' });
    myAmapFun.getRidingRoute({
      origin: p.longitude + ',' + p.latitude,
      destination: s.longitude + ',' + s.latitude,
      success: function (data) {
        var distance = ((data && data.paths[0].distance) / 1000).toFixed(1);
        console.log(distance);
        that.setData({
          distance: distance
        })
        typeof cb == "function" && cb();
      },
      fail: function (info) {
        console.log("fail")
      }
    })
  },

  // 提交订单
  createOrd: function () {
    var that = this,
      data = this.data,
      valid = this.examTime() && this.examAddress();
    if (!valid) {
      return false;
    }
    var _data = {
      "type": data.task_type,
      "amount": data.cost,
      "getTime": data.getTime,
      "markiUserFee": data.cost * data.costConfig.marki_user_rate / 100,
      "markiUserRate": data.costConfig.marki_user_rate,
      "mileageTotal": data.distance,
      "singleAmount": data.costConfig.more_km_price,
      "note": data.note || "",
      "startMileage": data.costConfig.start_km,
      "startAmount": data.costConfig.start_price,
      "f_area3": data.pick.adcode,
      "f_address": data.pick.areaName + data.pick.address,
      "f_latitude": data.pick.latitude,
      "f_longitude": data.pick.longitude,
      "f_name": data.pick.name,
      "f_phone": data.pick.phone,
      "t_area3": data.send.adcode,
      "t_name": data.send.name,
      "t_phone": data.send.phone,
      "t_address": data.send.areaName + data.send.address,
      "t_latitude": data.send.latitude,
      "t_longitude": data.send.longitude
    }
    var time = _data.getTime.split(" ")[1]
    var h = time.split(":")[0],
      m = time.split(":")[1],
      d = _data.getTime.split(" ")[0];
    d = d.split("-")[2]
    var now = new Date(), now_h = now.getHours(), now_m = now.getMinutes(), now_d = now.getDate()
    if (d == now_d && h == now_h && m < now_m) {
      wx.showToast({
        title: '取货时间不能早于当前时间',
        icon: "none"
      })
      return false;
    }
    service({
      url: '/order/create',
      data: _data,
      method: 'POST'
    }, res => {

      if(res.status === 302) {
        wx.showToast({
          title: '登录信息过期，请重新登录',
          icon: "none"
        });
        setTimeout(()=>{
          wx.redirectTo({
            url: '../../login/login'
          })
        }, 1500)
      } else if (res.responseCode === 'ER0001'){
        wx.showToast({
          title: res.responseMessage,
          icon: "none"
        });
      } else {
        wx.showToast({
          title: "订单创建成功",
          icon: "none"
        });
        that.setData({
          orderId: res.payload.orderId
        })
        console.log(res);
        console.log(that.data.orderId)
        setTimeout(()=> {
          this.setData({
            pay_show: true
          });
        }, 1000)

      }
   

      // that.setData({
      //   ordId: res.payload.id
      // })
      // var id = res.id
      // payment(res.id, () => {
      //   wx.navigateTo({
      //     url: '../custom/order/detail?id=' + res.id,
      //     success: function (res) {
      //       app.globalData.hasOrder = true;
      //     },
      //     fail: function (res) { },
      //     complete: function (res) { },
      //   })
      // })
    })
  },
  // 支付
  toPay: function() {
    this.setData({
      pay_show: false
    });
    console.log(this.data.orderId)
    if(this.data.orderId) {
      service({
        url: '/order/toPay',
        data: {orderId: this.data.orderId},
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
  cancelPay: function() {
    this.setData({
      pay_show: false
    });
  },

  examTime: function () {
    if (!this.data.get_time) {
      wx.showToast({
        title: '请选择取货时间',
        icon: "none"
      })
      return false;
    }
    return true;
  },
  examAddress: function () {
    if (!this.data.send || !this.data.pick) {
      wx.showToast({
        title: '请选择地址',
        icon: "none"
      })
      return false;
    }
    return true;
  }

})
