package com.express.web.module;

import com.express.domain.User;
import com.express.domain.UserInfo;
import com.express.exception.BusinessException;
import com.express.mvc.context.UserContext;
import com.express.mvc.filter.AccessTokenFilter;
import com.express.service.AdminService;
import com.express.service.UserService;
import com.express.web.support.ResponseResult;
import com.express.web.support.Result;
import org.nutz.dao.Cnd;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

@IocBean
@At("/api/v1/adm")
@Ok("json")
@Chain("crossOrigin")
public class AdminModule {

    @Inject
    protected UserService userService;

    @Inject
    protected AdminService adminService;


    // 上传 认证信息
    @At("/upload")

    @AdaptBy(type= UploadAdaptor.class,  args = { "${app.root}/WEB-INF/tmp" })
    //上传
    public Object html4(@Param("name") TempFile tmpFile, AdaptorErrorContext errCtx) {
        if (errCtx != null) {
            System.out.println(errCtx.getAdaptorErr().toString());
            return false;
        }
        if (tmpFile == null || tmpFile.getFile().length() < 1024) {
            return false;
        }

//        System.out.println(tmpFile.getMeta().getFileLocalName());


        File file = tmpFile.getFile();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println(Files.getSuffixName(file).toLowerCase());
        System.out.println(webPath(uuid));
        String dest = webPath(uuid) + "." + Files.getSuffixName(file).toLowerCase();
        System.out.println();
        String smallPath = webPath(uuid)  + Files.getSuffixName(file).toLowerCase();
        try {
            Images.zoomScale(file, new File(smallPath), 128, 128, Color.BLACK);
            file.renameTo(new File(dest));
        } catch (Throwable e) {
            System.out.println(e);
            return ResponseResult.newFailResult("上传失败");
        }
        String res = "/images/" + uuid + "."+ Files.getSuffixName(file).toLowerCase();
        HashMap <String,String> r = new HashMap<String ,String >();
        r.put("url",res);
        return ResponseResult.newResult(r);

    }

    public String webPath(String path) {
        return Mvcs.getServletContext().getRealPath("/images/") + "/"+path;
    }

    /**
     * 申请成为配送员
     * @param userInfo
     * @return
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/add")
    @AdaptBy(type = JsonAdaptor.class)
    public Result add(@Param("..") UserInfo userInfo)  {
        System.out.println(userInfo.toString());
        User user = UserContext.getCurrentuser().get();
        user = userService.fetchByCnd(Cnd.where("name","=", user.getName()).and("password", "=", user.getPassword()));
        System.out.println(user.toString());
        if(adminService.query(user.getId()) > 0) {
            return ResponseResult.newFailResult("您已经申请过，不能重复申请");
        }

        userInfo.setUserId(user.getId());
        try {
            adminService.insert(userInfo);
        } catch (Exception e) {
            return ResponseResult.newFailResult("申请单已提交");
        }
        return ResponseResult.newResult();
    }


    /**
     * 超级管理员获取用户列表
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/getUserList")
    @AdaptBy(type = JsonAdaptor.class)
    public Result getUserList() {
        return ResponseResult.newResult(userService.query());
    }


    /**
     * 获取审核列表
     * @return
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/getExamList")
    @AdaptBy(type = JsonAdaptor.class)
    public Result getExamList() {
        return ResponseResult.newResult(adminService.getExamList());
    }


    /**
     * 申请通过
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/pass")
    @AdaptBy(type = JsonAdaptor.class)
    public Result passExam(@Param("id") Long id) {
        try {
            adminService.pass(id);
            return ResponseResult.newResult("操作成功");
        } catch (BusinessException e) {
            return ResponseResult.newFailResult("操作失败");
        }


    }

    /**
     * 设置vip
     * @param id
     * @return
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/vip")
    @AdaptBy(type = JsonAdaptor.class)
    public Result setVip(@Param("id") Long id) {
        User user = userService.fetchById(id);
        user.setVip(true);
        userService.update(user, "^isVip$");
        return ResponseResult.newResult();
    }

    /**
     * 移除vip
     * @param id
     * @return
     */
    @POST
    @Filters(@By(type = AccessTokenFilter.class, args = {"ioc:tokenFilter"}))
    @At("/removeVip")
    @AdaptBy(type = JsonAdaptor.class)
    public Result removeVip(@Param("id") Long id) {
        User user = userService.fetchById(id);
        user.setVip(false);
        userService.update(user, "^isVip$");
        return ResponseResult.newResult();
    }

}
