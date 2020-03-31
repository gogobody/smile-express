package com.express.service;


import com.express.common.page.Pagination;
import com.express.domain.Income;
import com.express.domain.Order;
import com.express.domain.User;
import com.express.domain.UserOrder;
import com.express.exception.BusinessException;
import com.express.web.support.ResponseCodes;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Exps;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 订单业务层
 */
@IocBean(args = { "refer:dao" })
public class OrderService extends BaseService<Order>{

    public OrderService(Dao dao) {
        super(dao);
    }

    /**
     * 创建订单
     * @param order object
     * @param userId long
     * @throws Exception
     */
    public void insert(Order order, final Long userId) throws Exception{

        final Order _order = order;
        try {
            Trans.exec(new Atom(){
                public void run() {
                    dao().insert(_order);
                    //Sql sql = Sqls.create("INSERT INTO $table (orderId,userId) VALUES(@orderid,@userid)");
                    // 为变量占位符设值
                    //sql.vars().set("table","order_user");
                    // 为参数占位符设值
                    //sql.params().set("orderid",_order.getId()).set("userid",userId);
                    //dao().execute(sql);
                    UserOrder userOrder = new UserOrder();
                    userOrder.setOrderId(_order.getOrderId());
                    userOrder.setUserId(userId);

                    dao().insert(userOrder);

                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }

    }

    /**
     * 更新
     * @param order
     * @return
     */
    public int update(Order order) {
        order.setUpdateDate(new Date());
        return dao().update(order);
    }

    /**
     * 更新
     * @param order object
     * @param Regx  "^age$" "^age$" //正则
     * @return
     */
    public int update(Order order, String Regx) {

        return dao().update(order, Regx);
    }

    /**
     * 确认订单
     */
    public void confirmOrder(Order order, Long userId) {
        final Order _order = order;
        final Long _userId = userId;
        try {
            Trans.exec(new Atom(){
                public void run() {
                    dao().update(_order, "^orderStatus$");
                    dao().update(_order, "^finish_time$");
                    Income income = new Income();
                    income.setAmount(_order.getAmount());
                    income.setDesc("派送单收入");
                    income.setType(0);
                    income.setCreateDate(new Date());
                    dao().insert(income);
                    Sql sql = Sqls.create("INSERT INTO $table (incomeid,userid) VALUES(@incomeid,@userid)");
                    // 为变量占位符设值
                    sql.vars().set("table","user_income");
                    // 为参数占位符设值
                    sql.params().set("incomeid",income.getId()).set("userid",_userId);
                    dao().execute(sql);
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }
    }

    /**
     * 查找
     * @param orderId String
     * @return order of object
     */
    public Order find(String orderId) {
        return dao().fetch(Order.class, orderId);
    }

    /**
     * 分页获取订单列表
     * @param status
     * @param userId
     * @param page
     * @return
     */
    public List<Order> list(int status, Long userId, Pagination page) {
        //dao.createPager 第一个参数是第几页，第二参数是一页有多少条记录
        //orderStatus;  // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消
        //List<UserOrder> userOrderList = dao().query(UserOrder.class, Cnd.where("userId", "=", userId));
        // 创建一个 Criteria 接口实例
        Criteria cri = Cnd.cri();

        if(userId == 0) { //则不受限于用户

        } else {
            cri.where().and(Exps.inSql("orderId", "select orderId from order_user where userId = "+userId));
        }

        // 组装条件
        if(status == 0) {  // 全部
//            cri.where().and("orderStatus", "<>", status);
        } else {
            // 如果查询已完成的，就把已完成和带评论的都返回
            if(status == 5){// 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
                cri.where().and("orderStatus", "=", status).or("orderStatus", "=", 4);

            } else {
                cri.where().and("orderStatus", "=", status);
            }
        }
        cri.getOrderBy().desc("id");
        List<Order> orderList = dao().query(Order.class, cri, dao().createPager(page.getPageNo(), page.getPageSize()));
        if(orderList != null) {
            String thisTime = "";
            long time = 0;
            long nowTime = (new Date()).getTime();
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for(int i = 0; i < orderList.size(); i++) {
                thisTime = orderList.get(i).getGetTime();
                //thisTime = thisTime.replace(/-/g, '/');
                try {
                    Date date=simpleDateFormat.parse(thisTime);
                    time = date.getTime();
                    if (time - nowTime < 0) {  // 时间超时自动取消
                        orderList.get(i).setOrderStatus(6);
                        this.update(orderList.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return orderList;
    }

    /**
     * 分页获取评论订单列表
     * @param status
     * @param userId
     * @param page
     * @return
     */
    public List<Order> clist(int status, Long userId, Pagination page) {
        //dao.createPager 第一个参数是第几页，第二参数是一页有多少条记录
        //orderStatus;  // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消
        //List<UserOrder> userOrderList = dao().query(UserOrder.class, Cnd.where("userId", "=", userId));
        // 创建一个 Criteria 接口实例
        Criteria cri = Cnd.cri();

        if(userId == 0) { //则不受限于用户

        } else {
            cri.where().and(Exps.inSql("orderId", "select orderId from order_user where sellerId = "+userId));
        }

        // 组装条件
        if(status == 0) {  // 全部

        } else {
            // 如果查询已完成的，就把已完成和带评论的都返回
            if(status == 5){// 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
                cri.where().and("orderStatus", "=", status).or("orderStatus", "=", 4);

            } else {
                cri.where().and("orderStatus", "=", status);
            }
        }
        cri.getOrderBy().desc("id");
        List<Order> orderList = dao().query(Order.class, cri, dao().createPager(page.getPageNo(), page.getPageSize()));
        if(orderList != null) {
            String thisTime = "";
            long time = 0;
            long nowTime = (new Date()).getTime();
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for(int i = 0; i < orderList.size(); i++) {
                thisTime = orderList.get(i).getGetTime();
                //thisTime = thisTime.replace(/-/g, '/');
                try {
                    Date date=simpleDateFormat.parse(thisTime);
                    time = date.getTime();
                    if (time - nowTime < 0) {  // 时间超时自动取消
                        orderList.get(i).setOrderStatus(6);
                        this.update(orderList.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return orderList;
    }

    public List<Order> list(int status, Long userId, Pagination page, int tag) {
        //dao.createPager 第一个参数是第几页，第二参数是一页有多少条记录
        //orderStatus;  // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消
        //List<UserOrder> userOrderList = dao().query(UserOrder.class, Cnd.where("userId", "=", userId));
        // 创建一个 Criteria 接口实例
        Criteria cri = Cnd.cri();

        if(userId == 0) { //则不受限于用户

        } else if(tag == 1){
            cri.where().and(Exps.inSql("orderId", "select orderId from order_user where sellerId = "+userId));
        }

        // 组装条件
        if(status == 0) {  // 全部1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
            cri.where().and("orderStatus", "=", 3).and("orderStatus", "=", 4).and("orderStatus", "=", 5);
        } else {
            cri.where().and("orderStatus", "=", status);
        }
        cri.getOrderBy().desc("id");
        List<Order> orderList = dao().query(Order.class, cri, dao().createPager(page.getPageNo(), page.getPageSize()));
        if(orderList != null) {
            String thisTime = "";
            long time = 0;
            long nowTime = (new Date()).getTime();
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for(int i = 0; i < orderList.size(); i++) {
                thisTime = orderList.get(i).getGetTime();
                //thisTime = thisTime.replace(/-/g, '/');
                try {
                    Date date=simpleDateFormat.parse(thisTime);
                    time = date.getTime();
                    if (time - nowTime < 0) {  // 时间超时自动取消
                        orderList.get(i).setOrderStatus(6);
                        this.update(orderList.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return orderList;
    }

    /**
     * 接单业务
     * @param orderId string
     * @param user user
     */
    public void receiveOrder(String orderId, User user) throws BusinessException{

        if(!user.isSuper() && user.isTaker() && !user.isVip()) {
            if(user.getIsAble() > 0) {
                user.setIsAble(user.getIsAble()-1);
                System.out.println(user.getIsAble());
                final String _orderId = orderId;
                final User _user = user;
                try {
                    Trans.exec(new Atom() {
                        public void run() {
                            dao().update(_user,  "^isAble$");
                            Order ord = dao().fetch(Order.class, _orderId);
                            ord.setOrderStatus(2); // 待取货
                            dao().update(ord, "^orderStatus$");
                            UserOrder userOrder = dao().fetch(UserOrder.class, _orderId);
                            userOrder.setSellerId(_user.getId());
                            dao().update(userOrder);
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.toString());
                    throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
                }

            } else {
                user.setIsTipTime(new Date());
                dao().update(user,  "^isTipTime$");
                throw new BusinessException("您的接单次数已达到上线");
            }
        } else if(user.isSuper() || (user.isTaker() && user.isVip())){
            final String _orderId = orderId;
            final User _user = user;
            try {
                Trans.exec(new Atom() {
                    public void run() {
                        Order ord = dao().fetch(Order.class, _orderId);
                        ord.setOrderStatus(3);
                        dao().update(ord, "^orderStatus$");
                        UserOrder userOrder = dao().fetch(UserOrder.class, _orderId);
                        userOrder.setSellerId(_user.getId());
                        dao().update(userOrder, "^sellerId$");
                    }
                });
            } catch (Exception e) {
                System.out.println(e.toString());
                throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
            }

        }
    }

    /**
     * 确认取到货
     * @param orderId string
     */
    public void confirmGood(String orderId) throws BusinessException{
        try {
            Trans.exec(new Atom() {
                public void run() {
                    Order ord = dao().fetch(Order.class, orderId);
                    ord.setOrderStatus(3); // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
                    dao().update(ord, "^orderStatus$");
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }
    }

    /**
     * 取消订单
     * @param orderId string
     */
    public void cancelOrd(String orderId) throws BusinessException{
        try {
            Trans.exec(new Atom() {
                public void run() {
                    Order ord = dao().fetch(Order.class, orderId);
                    ord.setOrderStatus(6); // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
                    dao().update(ord, "^orderStatus$");
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }
    }

    /**
     * 获取userorder
     * @return
     */
    public UserOrder fetchUserOrderById(String orderId) {
        return dao().fetch(UserOrder.class, orderId);
    }

    /**
     * 添加评论
     * @param order order
     */
    public void addComment(Order order) throws BusinessException{
        try {
            Trans.exec(new Atom() {
                public void run() {
                    order.setOrderStatus(5); // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消 7待派单
                    dao().update(order, "^orderStatus$");
                    dao().update(order,"^content$");
                    dao().update(order,"^label$");
                    dao().update(order,"^score$");
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }


    }
}
