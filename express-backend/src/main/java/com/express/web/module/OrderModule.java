package com.express.web.module;
import com.express.common.page.Pagination;
import com.express.domain.Order;
import com.express.domain.User;
import com.express.domain.UserOrder;
import com.express.exception.BusinessException;
import com.express.mvc.context.UserContext;
import com.express.mvc.filter.AccessTokenFilter;
import com.express.service.OrderService;
import com.express.service.UserOrderService;
import com.express.service.UserService;
import com.express.utils.CommonUtils;
import com.express.utils.ReflectUtil;
import com.express.web.support.ResponseResult;
import com.express.web.support.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单模块
 */

@IocBean
@At("/api/v1/order")
@Ok("json") //@Ok - 成功视图
@Chain("crossOrigin")
public class OrderModule {


    @Inject
    protected OrderService orderService;

    @Inject
    protected UserService userService;

    @Inject
    protected UserOrderService userOrderService;

    /**
     * 创建订单
     * @param order object
     * @return Result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/create")
    @AdaptBy(type = JsonAdaptor.class)
    public Result create(@Param("..") Order order) { //值 ".." 有特殊含义，表示当前的这个对象
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        try {
            order.setOrderId(CommonUtils.getOrderIdByTime());
            order.setCreateDate(new Date());
            order.setOrderStatus(1); // 待支付状态
            orderService.insert(order, user.getId());

        } catch (Exception e) {
            return ResponseResult.newFailResult("订单创建失败");
        }
        return ResponseResult.newResult(order);
    }

    /**
     * 模拟支付
     * @param orderId id
     * @return Result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/toPay")
    @AdaptBy(type = JsonAdaptor.class)
    public Result toPay(@Param("orderId") String orderId) {
        Order order = orderService.find(orderId);
        if(order != null && order.getOrderStatus() == 1) {
            order.setOrderStatus(7); // 设置为待派单状态
            order.setCreateDate(new Date());
            int count = orderService.update(order, "^orderStatus$");
            if(count > 0) {
                return ResponseResult.newResult(order.getOrderId());  // 若支付成功则返回订单号
            } else {
                return ResponseResult.newFailResult("支付失败！");
            }
        } else {
            return ResponseResult.newFailResult("不存在该支付订单");
        }
    }

    /**
     *
     * @param status 订单状态
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return Result
     */
    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/list")
    @AdaptBy(type = JsonAdaptor.class)
    public Result list(@Param("status") int status, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        Pagination page = new Pagination();
        if(pageNo == 0) pageNo = 1;
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        List<Order> list = orderService.list(status, user.getId(), page);
        page.setList(list);
        //page.setTotalCount(list.size());
        return ResponseResult.newResult(page);
    }

    /**
     * 获取评论列表
     * @param status 订单状态
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return Result
     */
    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/clist")
    @AdaptBy(type = JsonAdaptor.class)
    public Result clist(@Param("status") int status, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        Pagination page = new Pagination();
        if(pageNo == 0) pageNo = 1;
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        List<Order> list = orderService.clist(status, user.getId(), page);
        page.setList(list);
        //page.setTotalCount(list.size());
        return ResponseResult.newResult(page);
    }


    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/find")
    @AdaptBy(type = JsonAdaptor.class)
    public Result find(@Param("orderId") String orderId) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        if(orderId != null) {
            Order order = orderService.find(orderId);
            User user = userOrderService.sellerGetuser(orderId);
            Map<String,Object> properties = Maps.newHashMap();
            properties = ReflectUtil.transBean2Map(order);
            try{
                properties.put("user_name",user.getName());
//            properties.put("user_phone",user.get());
            }catch (java.lang.NullPointerException ignored){
                properties.put("user_name","");
            }
            User seller = userOrderService.userGetseller(orderId);
            System.out.println(seller);
            if(seller!=null){
                properties.put("seller_name",seller.getName());
                properties.put("seller_phone",seller.getPhone());
            }else {
                properties.put("seller_name","");
                properties.put("seller_phone","");
            }
//            try {
//                properties.put("seller_name",seller.getName());
//
//            }catch (java.lang.NullPointerException ignored){
//                properties.put("seller_name","");
//            }
//            try {
//                properties.put("seller_phone",seller.getPhone());
//
//            }catch (java.lang.NullPointerException ignored){
//                properties.put("seller_phone","");
//            }

            return ResponseResult.newResult(properties);
        }
        return ResponseResult.newFailResult("地址id无效");
    }

    /**
     * 客户确认收货
     * @param orderId string
     * @return result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/confirmOrder")
    @AdaptBy(type = JsonAdaptor.class)
    public Result confirmOrder(@Param("orderId") String  orderId) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        UserOrder userOrder =  orderService.fetchUserOrderById(orderId);

        if(orderId != "") {
            Order order = orderService.find(orderId);
            order.setOrderStatus(4); // 待评价
            order.setFinish_time(new Date().toString());
            try {
                orderService.confirmOrder(order, userOrder.getSellerId());
                return ResponseResult.newResult();
            } catch (BusinessException e) {
                return ResponseResult.newFailResult(e.getMessage());
            }

            /*if(orderService.update(order, "^orderStatus$") > 0) {
                return ResponseResult.newResult();
            } else {
                return ResponseResult.newFailResult("确认收货失败");
            }*/
        } else {
            return ResponseResult.newFailResult("无效订单号");
        }
    }


    /**
     * 配送员
     * 抢单列表
     * @param status 订单状态
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return Result
     */
    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/gradList")
    @AdaptBy(type = JsonAdaptor.class)
    public Result gradList(@Param("status") int status, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        // 不是超级管理员，但是是业务员却不是vip的情况下
        if(!user.isSuper() && user.isTaker() && !user.isVip()) {
            //时间格式化
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if(!CommonUtils.isToday(user.getIsTipTime())) { //时间输出为true，则处于当天24h范围内，false反之
                    user.setIsAble(5);  // 设置可抢单5次
                    user.setIsTipTime(new Date());
                    userService.update(user);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        Pagination page = new Pagination();
        if(pageNo == 0) {
            pageNo = 1;
        }
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        List<Order> list = null;
        if(status == 2) { //改成了查看所有 // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
            list = orderService.list(status, user.getId(), page, 1);  // seller
        } else {
            list = orderService.list(status, (long) 0, page);
        }

        page.setList(list);
        return ResponseResult.newResult(page);
    }


    /**
     * 配送员确认接单
     * @param orderId string
     * @return result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/confirm")
    @AdaptBy(type = JsonAdaptor.class)
    public Result confirm(@Param("orderId") String orderId) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));

        try {
            orderService.receiveOrder(orderId, user);
            return ResponseResult.newResult();
        } catch (BusinessException e) {
            return ResponseResult.newFailResult(e.getMessage());
        }
    }

    /**
     * 配送员确认取到货
     * @param orderId string
     * @return result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/sconfirm")
    @AdaptBy(type = JsonAdaptor.class)
    public Result sconfirm(@Param("orderId") String orderId) {
        try {
            orderService.confirmGood(orderId);
            return ResponseResult.newResult();
        } catch (BusinessException e) {
            return ResponseResult.newFailResult(e.getMessage());
        }
    }

    /**
     * 取消订单
     * @param orderId string
     * @return result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/cancel")
    @AdaptBy(type = JsonAdaptor.class)
    public Result cancel(@Param("orderId") String orderId) {
        try {
            orderService.cancelOrd(orderId);
            return ResponseResult.newResult();
        } catch (BusinessException e) {
            return ResponseResult.newFailResult(e.getMessage());
        }
    }

    /**
     * 添加评论
     * @param orderId string
     * @return result
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/addComment")
    @AdaptBy(type = JsonAdaptor.class)
    public Result addComment(@Param("orderId") String  orderId, @Param("label") String label,
                             @Param("content") String content, @Param("score") Integer score) {

        if(orderId != "") {
            Order order = orderService.find(orderId);
            if(order!=null){
                order.setOrderStatus(5); // 已完成
                order.setLabel(label);
                order.setContent(content);
                order.setScore(score);

            }else {
                return ResponseResult.newFailResult("没有这个订单");
            }

            try {
                orderService.addComment(order);
                return ResponseResult.newResult();
            } catch (BusinessException e) {
                return ResponseResult.newFailResult(e.getMessage());
            }

            /*if(orderService.update(order, "^orderStatus$") > 0) {
                return ResponseResult.newResult();
            } else {
                return ResponseResult.newFailResult("确认收货失败");
            }*/
        } else {
            return ResponseResult.newFailResult("无效订单号");
        }
    }


}
