package com.express;
import com.express.domain.*;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import java.util.Date;

/**
 * @description: MainSetup 需要实现Setup接口,并在其中初始化数据库表
 */
public class MainSetup implements Setup {


    // 特别留意一下,是init方法,不是destroy方法!!!!!
    public void init(NutConfig nc) {
        System.out.println("setup::init");

        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class);
        // 如果没有createTablesInPackage,请检查nutz版本
        Daos.createTablesInPackage(dao, "com.express.domain", false);
        Daos.migration(dao, User.class, true, true, true);
        Daos.migration(dao, Address.class, true, true, true);
        Daos.migration(dao, Order.class, true, true, true);
        Daos.migration(dao, UserOrder.class, true, true, true);
        Daos.migration(dao, Permission.class, true, true, true);
        Daos.migration(dao, UserInfo.class, true, true, true);

        // 初始化默认根用户
        if (dao.count(User.class) == 0) {
            User user = new User();
            user.setName("admin");
            user.setPassword(Lang.md5("123456"));
            user.setSuper(true);
            //user.setCreateTime(new Date());
            user.setCreateDate(new Date());
            dao.insert(user);
        }
        // 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
        //ioc.get(NutQuartzCronJobFactory.class);

    }

    public void destroy(NutConfig nc) {        // webapp销毁之前执行的逻辑
        // 这个时候依然可以从nc取出ioc, 然后取出需要的ioc 对象进行操作
        System.out.println("setup::destroy");
    }

}
