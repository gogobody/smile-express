package com.express.mvc.context;


import com.express.domain.User;

/**
 * @description: 线程中保存token ,保证登录用户信息解析后存在同一个线程中共享数据信息
 */
public class UserContext {
    static final ThreadLocal<User> currentUser = new ThreadLocal<User>();
    //用来保存信息的
    static final ThreadLocal<String> loginInfo = new ThreadLocal<String>();

    public UserContext(String logininfo){
        loginInfo.set(logininfo);
    }
    public static ThreadLocal<String> getLoginInfo(){
        return loginInfo;
    }
    public UserContext(User user, String logininfo){
        loginInfo.set(logininfo);
        currentUser.set(user);
    }


    public UserContext(User user){
        currentUser.set(user);
    }
    public static ThreadLocal<User> getCurrentuser() {
        return currentUser;
    }
    public void close() throws Exception {
        currentUser.remove();
        loginInfo.remove();
    }
}
