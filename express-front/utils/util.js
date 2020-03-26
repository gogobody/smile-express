const app = getApp();
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}


const service = (options, callback, error) => {
  let token = wx.getStorageSync('token')
  if (!options.hideLoading) {
    wx.showLoading({
      title: '加载中',
      mask: true
    })
  }
  wx.request({
    url: app.globalData.baseUrl + options.url,
    data: options.data || '',
    method: options.method || 'GET',
    header: {
      //'content-type': options.method == 'POST' ? 'application/x-www-form-urlencoded' : 'application/json', // 默认值
      'content-type': 'application/json',
      'Authorization': token
    },
    success: (res) => {
      // wx.hideLoading();
      if (!options.hideLoading) {
        wx.hideLoading()
      }
      callback(res.data);

      // if (res.data.retCode === 1 || res.data.retCode === 3) {
      //   callback(res.data.retObj, res.data.retCode);
      // } else if (res.data.retCode === 2) {
      //   // 重新登录
      //   wxLogin();
      //   service(options, callback);
      // } else if (res.data.retCode === -1) {
      //   // error

      //   typeof error == "function" && error(res.data.retObj);
      // }
    },
    complete: function () {

    }
  })
}

module.exports = {
  formatTime: formatTime,
  service: service
}
