const app = getApp()
const util = require('../../../utils/util.js');
const service = util.service;
Page({
  data: {
    currentPage:1
  },
  onLoad: function (options) {
    this.getList()
  },
  onShow: function () {

  },
  getList:function(){
    var that = this;
    service({
      url:'/order/clist',
      data: {
        status: 5,
        pageSize: 100,
        pageNo: 1
      },
      method:'GET'
    },res=>{
      if(!res.payload.list.length){
        that.setData({
          score:null
        })
        return false;
      }
      that.setData({
        score:res.payload.list
      })
    })
  },
  loadMore:function(){
    var that = this,
      currentPage = this.data.currentPage,
      list = this.data.score || [];
    service({
      url: '/order/clist',
      method: 'GET',
      data:{
        status:5,
        pageNo: currentPage,
        pageSize:15
      }
    },res=>{
      if (!res.payload.list){
        return false;
      }
      list = list.concat(res.payload.list);
      currentPage++;
      that.setData({
        currentPage:currentPage,
        score: list
      })
    })
  }
})