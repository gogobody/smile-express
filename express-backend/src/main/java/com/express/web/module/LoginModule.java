package com.express.web.module;

import com.express.common.page.Pagination;
import com.express.domain.Income;
import com.express.domain.User;
import com.express.mvc.context.UserContext;
import com.express.mvc.filter.AccessTokenFilter;
import com.express.security.JwtToken;
import com.express.service.IncomeService;
import com.express.service.UserService;
import com.express.utils.GetOpenIDUtil;
import com.express.web.support.ResponseResult;
import com.express.web.support.Result;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

//import javax.xml.ws.Response;
import java.util.List;


/**
 * @user: 180296-Web寻梦狮
 * @description: 登录模块
 */
@IocBean
@At("/api/v1/login")
@Ok("json")
@Chain("crossOrigin")
public class LoginModule {

    @Inject
    protected UserService userService;

    @Inject
    protected IncomeService incomeService;

    @POST
    //@Filters(@By(type = CrossOriginsFilter.class,args = {"ioc:crossFilter"}))
    @At("/login")
    public Result doLogin(@Param("username")  String name, @Param("password") String password) {

        return this.validUser(name, password);
    }


    /**
     * 绑定用户信息
     * @param role 角色role
     * @param name 用户名
     * @param password 密码
     * @param code 微信小程序的code用于获取openId
     * @return
     */
    @POST
    @At("/bindUser")
    @AdaptBy(type = JsonAdaptor.class)  //适配器接收 "Content-Type": "applciation/json" ；若无，则默认接收x-wwww-encode
    public Result bindUser(@Param("role") String role, @Param("username") String name , @Param("password") String password,
                           @Param("code") String code) {
        System.out.println(name);
        System.out.println(password);
        Result res =  this.validUser(name, password, role);
        if (code == null || code.length() == 0) {
            return ResponseResult.newFailResult("code不能为空");
        } else {
            if(res == null) {
                NutMap resultMap = GetOpenIDUtil.oauth2GetOpenid(GetOpenIDUtil.appID, code, GetOpenIDUtil.appSecret);
                if(resultMap.has("errcode")) {
                    return ResponseResult.newFailResult(resultMap.getString("errmsg"));
                } else {
                    System.out.println(resultMap.getString("openid"));

                    //判断该openId是否已经绑定其他账号
                    User u = userService.fetchByOpenId(resultMap.getString("openid"));
                    if(u != null ) {
                        return ResponseResult.newFailResult("该openId已绑定其他账号");
                    }

                    User exist = userService.fetchByCnd(Cnd.where("name", "=",name).and("password","=", Lang.md5(password)));
                    exist.setOpenId(resultMap.getString("openid"));
                    userService.update(exist);
                    return ResponseResult.newResult();
                }
            } else {
                return res;
            }
        }
    }

    /**
     * 获取用户信息
     * @return User
     */
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))  // 必须提供token才能通过，即前端需要登录
    @At("/getUserInfo")
    @POST
    public Result getUserInfo() {
        User user = UserContext.getCurrentuser().get();
        System.out.println(user.toString());
        if(user != null) {
            User result = userService.fetchByCnd(Cnd.where("name", "=",user.getName()).and("password","=", user.getPassword()));
            if(result != null) return  ResponseResult.newResult(result);
        }
        return ResponseResult.newFailResult("不存在相应的用户或token已过期");
    }


    /**
     * 通过openId登录
     * @param code
     * @return token
     */
    @POST
    @At("/loginByCode")
    @AdaptBy(type = JsonAdaptor.class)
    public Result loginByOpenId(@Param("code") String code) {
        System.out.println(code);
        if(code != "") {
            NutMap resultMap = GetOpenIDUtil.oauth2GetOpenid(GetOpenIDUtil.appID, code, GetOpenIDUtil.appSecret);
            User user = userService.fetchByCnd(Cnd.where("openId", "=", resultMap.getString("openid")));
            if(user != null) {
                System.out.println(user);
                return ResponseResult.newResult(user).setHeader("token", JwtToken.createToken(user));
            } else {
                return ResponseResult.newFailResult("无效openId");
            }

        } else {
            return ResponseResult.newFailResult("openId获取失败");
        }

    }


    /**
     * 验证用户数据
     * @param name 用户名
     * @param password 密码
     * @return
     */
    public Result validUser(String name, String password) {
        if(Strings.isBlank(name) || Strings.isBlank(password)){
            return ResponseResult.newFailResult("用户名或密码不能为空");
        }
        User exist = userService.fetchByName(name);
        if (exist == null) {
            return ResponseResult.newFailResult("用户名不存在");
        }



        User user = userService.fetchByName(name);
        if (user != null) {
            if(user.getPassword().equalsIgnoreCase(Lang.md5(password))) {
                return ResponseResult.newResult(user).setHeader("token", JwtToken.createToken(user));
            } else {
                return ResponseResult.newFailResult("登录密码错误");
            }
        } else {
            return ResponseResult.newFailResult("不存在该用户");
        }
    }

    /**
     * 验证用户数据
     * @param sname 用户名
     * @param passwords 密码
     * @param role 角色
     * @return
     */
    public Result validUser(String sname, String passwords, String role) {
        if(Strings.isBlank(sname) || Strings.isBlank(passwords)){
            return ResponseResult.newFailResult("用户名或密码不能为空");
        }
        User exist = userService.fetchByName(sname);
        if (exist == null) {

            return ResponseResult.newFailResult("用户名不存在");
        } else {
            System.out.println(exist.getOpenId());
            if(exist.getOpenId() != null && !exist.getOpenId().equals("")) {
                return ResponseResult.newFailResult("该账号已绑定过其他微信号");
            }

        }
        System.out.println(role);
        if(role != null) {
            String message = "";
            switch (role) {
                case "超级管理员":
                    System.out.println("A");
                    if(!exist.isSuper()) {
                        message = "该角色下不存在对应用户";
                    }
                    break;
                case "配送员":
                    System.out.println("B");
                    if(!exist.isTaker()) {
                        message = "该角色下不存在对应用户";
                    }
                    break;
                case "普通用户":
                    System.out.println("C");
                    if(exist.isSuper() || exist.isTaker() ) {
                        message = "该角色下不存在对应用户";
                    }
                default:
                    break;
            }
            if(message != "") {
                return ResponseResult.newFailResult(message);
            } else {
                System.out.println(passwords);
                if(!exist.getPassword().equalsIgnoreCase(Lang.md5(passwords))) {
                    return ResponseResult.newFailResult("密码错误");
                }
            }

        }

        return null;
    }

    @POST
    @At("/registerUser")
    @AdaptBy(type = JsonAdaptor.class)
    public Result registerUser(@Param("username") String name, @Param("password") String pwd, @Param("phone") String phone ) {
        if(name != "" && pwd != "") {
            User user = new User();
            user.setName(name);
            user.setPassword(Lang.md5(pwd));
            user.setPhone(phone);
            userService.insert(user);
            return ResponseResult.newResult();
        } else {
            return ResponseResult.newFailResult("用户名、密码、电话不能为空");
        }
    }




    /**
     * 获取收入明细列表
     * 明细列表
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return Result
     */
    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/getIncomeList")
    @AdaptBy(type = JsonAdaptor.class)
    public Result getIncomeList(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        Pagination page = new Pagination();
        if(pageNo == 0) {
            pageNo = 1;
        }
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        List<Income> list = null;
        list = incomeService.list(user.getId(), page);
        page.setList(list);
        return ResponseResult.newResult(page);
    }
    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/getIncomeAmount")
    @AdaptBy(type = JsonAdaptor.class)
    public Result getIncomeAmount() {
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));

        return ResponseResult.newResult(incomeService.amount(user.getId()));
    }
}
