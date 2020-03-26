package com.express.web.module;

import com.express.domain.Address;
import com.express.domain.User;
import com.express.mvc.context.UserContext;
import com.express.mvc.filter.AccessTokenFilter;
import com.express.service.AddressService;
import com.express.service.UserService;
import com.express.web.support.ResponseResult;
import com.express.web.support.Result;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

/**
 * @user: 180296-Web寻梦狮
 * @description: 登录模块
 */

@IocBean
@At("/api/v1/address")
@Ok("json")
@Chain("crossOrigin")
public class AddressModule {

    @Inject
    protected AddressService addressService;

    @Inject
    protected UserService userService;

    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/add")
    @AdaptBy(type = JsonAdaptor.class)
    public Result add(@Param("..") Address addr)  {
        System.out.println(addr.toString());
        User user = UserContext.getCurrentuser().get();
        System.out.println(user.toString());

        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        System.out.println(addr.toString());

        try {
            addressService.insert(addr, user.getId());
        } catch (Exception e) {
            return ResponseResult.newFailResult("新增地址失败");
        }
        return ResponseResult.newResult();
    }

    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/edit")
    @AdaptBy(type = JsonAdaptor.class)
    public Result edit(@Param("..") Address addr) {
        if(addr.getAddress().equalsIgnoreCase("")) {
            return ResponseResult.newFailResult("详细地址不能为空");
        }
        if(addressService.update(addr) == 1) {
            return ResponseResult.newResult();
        } else {
            return ResponseResult.newFailResult("添加地址失败！");
        }

    }




    @GET
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/list")
    @AdaptBy(type = JsonAdaptor.class)
    public Result query(@Param("areaName") String areaName)  {
        System.out.println("areaName:"+areaName);
        User user = UserContext.getCurrentuser().get();
        System.out.println(user);
        Address address = new Address();
        address.setAreaName(areaName == null ? "" : areaName);
        return ResponseResult.newResult(addressService.list(address, user.getId()));
    }

    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/delete")
    @AdaptBy(type = JsonAdaptor.class)
    public Result delete(@Param("id") Long addressId) {
        System.out.println("addressId:"+ addressId);
        try {
            addressService.delete(addressId);
            return ResponseResult.newResult();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.newFailResult("删除地址失败！");
        }
    }

    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/find")
    @AdaptBy(type = JsonAdaptor.class)
    public Result find(@Param("id") Long addressId) {
       if(addressId != null) {
           return ResponseResult.newResult(addressService.find(addressId));
       }
       return ResponseResult.newFailResult("地址id无效");
    }


}
