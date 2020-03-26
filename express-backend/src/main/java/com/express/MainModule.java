package com.express;
import org.nutz.mvc.annotation.*;

import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * @Author :gogobody
 * @Date Created in 下午1:43 2020/3/26
 * @Description: 主入口模块
 */

@ChainBy(args = "mvc/mvc-chain.js")
@Fail("jsp:jsp.500")
@Localization(value = "language/", defaultLocalizationKey = "zh-CN")
@IocBy(type = ComboIocProvider.class, args = {"*js", "ioc/", "*anno", //设置应用所采用的 Ioc 容器
        "com.errand", //搜索该包下的IocBean
        "*tx", // 事务拦截 aop
        "*async" // 异步执行aop
        }) //"*quartz" 即添加了 org.nutz.integration.quartz.QuartzIocLoader 这个预定义的集成配置
@Modules(scanPackage = true) ////1.r.58开始默认就是true
@SetupBy(value = MainSetup.class) // 应用启动以及关闭时的额外处理
public class MainModule {

}
