package com.express.service;

import com.express.domain.User;
import com.express.domain.UserOrder;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = { "refer:dao" })
public class UserOrderService extends BaseService<UserOrder>  {
    public UserOrderService(Dao dao) {
        super(dao);
    }

    public User userGetseller(String orderId){
        UserOrder userOrder = dao().fetch(UserOrder.class, orderId);
        User seller = dao().fetch(User.class, userOrder.getSellerId());
        return seller;
    }

    public User sellerGetuser(String orderId){
        UserOrder userOrder = dao().fetch(UserOrder.class, orderId);
        User user = dao().fetch(User.class, userOrder.getUserId());
        return user;
    }

}
