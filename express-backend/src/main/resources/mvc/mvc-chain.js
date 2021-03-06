/**
 *@description: mvc-chain 这个动作链配置文件,与nutz默认的配置文件,仅仅多了一行mvc.LogTimeProcessor
 *  "!org.nutz.integration.shiro.NutShiroProcessor",
 */

var chain={
    "default" : {
        "ps" : [
            "com.express.mvc.LogTimeProcessor",
            "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
            "org.nutz.mvc.impl.processor.EncodingProcessor",
            "org.nutz.mvc.impl.processor.ModuleProcessor",
            "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
            "org.nutz.mvc.impl.processor.AdaptorProcessor",
            "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
            "org.nutz.mvc.impl.processor.ViewProcessor"
        ],
        "error" : 'org.nutz.mvc.impl.processor.FailProcessor'
    },
    "crossOrigin" : {
        "ps" : [
            "com.express.mvc.LogTimeProcessor",
            "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
            "org.nutz.mvc.impl.processor.EncodingProcessor",
            "org.nutz.mvc.impl.processor.ModuleProcessor",
            "com.express.mvc.CrossOriginProcessor",
            "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
            "org.nutz.mvc.impl.processor.AdaptorProcessor",
            "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
            "org.nutz.mvc.impl.processor.ViewProcessor"
        ],
        "error" : 'org.nutz.mvc.impl.processor.FailProcessor'
    }
};