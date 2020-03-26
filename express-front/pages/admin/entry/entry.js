
const util = require('../../../utils/util.js');
const service = util.service;
const { $Toast } = require('../../../dist/base/index');
Page({
  data: {
    current: 'tab1',
    userList: [],
    examList: []
  },
  onLoad: function (options) { 
    this.initData('tab1');
  },
  handleChange({ detail }) {
    this.setData({
      current: detail.key
    });
    if(detail.key=='tab1'){
      this.initData('tab1');
    } else {
      this.initData('tab2');
    }
  },
  onChange(event) {
    const detail = event.detail;
    this.setData({
      'switch1': detail.value
    })

  },
  initData: function(status) {
    if(status=='tab1') {
      service({
        url: '/adm/getUserList',
        method: 'POST'
      }, (data) => {
        console.log(data);
        if (data.responseCode == "SC0000") {
          this.setData({
            userList: data.payload
          })
        } else {
          $Toast({
            content: data.responseMessage,
            type: 'error'
          });
        }
      })
    } else if(status == 'tab2') {
      service({
        url: '/adm/getExamList',
        method: 'POST'
      }, (data) => {
        console.log(data);
        if (data.responseCode == "SC0000") {
          this.setData({
            examList: data.payload
          })
        } else {
          $Toast({
            content: data.responseMessage,
            type: 'error'
          });
        }
      })
    }
    
  },
  passExam($event) {
    console.log($event)
    var _data = {
      id: $event.currentTarget.dataset.index
    }
    service({
      url: '/adm/pass',
      data: _data,
      method: 'POST'
    }, (data) => {
      console.log(data);
      if (data.responseCode == "SC0000") {
        $Toast({
          content: '操作成功！',
          type: 'success'
        });
        this.initData('tab2');
      } else {
        $Toast({
          content: data.responseMessage,
          type: 'error'
        });
      }
    })
  },
  setVip($event) {
    var _data = {
      id: $event.currentTarget.dataset.index
    }
    service({
      url: '/adm/vip',
      data: _data,
      method: 'POST'
    }, (data) => {
      console.log(data);
      if (data.responseCode == "SC0000") {
        $Toast({
          content: '操作成功！',
          type: 'success'
        });
        this.initData('tab1');
      } else {
        $Toast({
          content: data.responseMessage,
          type: 'error'
        });
      }
    })
   
  },
  removeVip($event) {
    var _data = {
      id: $event.currentTarget.dataset.index
    }
    service({
      url: '/adm/removeVip',
      data: _data,
      method: 'POST'
    }, (data) => {
      console.log(data);
      if (data.responseCode == "SC0000") {
        $Toast({
          content: '操作成功！',
          type: 'success'
        });
        this.initData('tab1');
      } else {
        $Toast({
          content: data.responseMessage,
          type: 'error'
        });
      }
    })

  },

});