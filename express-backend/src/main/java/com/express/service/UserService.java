package com.express.service;

import com.express.domain.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * User类的业务操作
 * create - 当对象被创建时触发
 * fetch - 当对象被从容器中取出时触发
 * depose - 当对象被销毁时触发
 */
@IocBean(args = { "refer:dao" })
public class UserService extends BaseService<User> {

    public UserService(Dao dao) {
        super(dao);
    }

    public List<User> list() {
        return query(null, null);
    }

    public void update(User user) {
        dao().update(user);
    }

    /**
     * 更新
     * @param user object
     * @param Regx  "^age$" "^age$" //正则
     * @return
     */
    public int update(User user, String Regx) {

        return dao().update(user, Regx);
    }

    public void insert(User user) {
        user = dao().insert(user);
        // dao().insertRelation(user, "roles");
        System.out.println(user);
    }

    /**
     * 根据用户名查询用户信息
     * @param name
     * @return
     */
    public User fetchByName(String name) {
        return fetch(Cnd.where("name", "=", name));
    }

    public User fetchByOpenId(String name) {
        return fetch(Cnd.where("openId", "=", name));
    }

    public User fetchById(Long id) {
        return dao().fetch(User.class, id);
    }

    public User fetchByCnd(Condition cnd) {
        return fetch(cnd);
    }

    /**
     * 获取用户列表
     * @return
     */
    public List<User>  query() {
        return dao().query(User.class, Cnd.where("is_super", "!=", 1));
    }

}
