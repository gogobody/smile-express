package com.express.mvc;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author :gogobody
 * @Date Created in 下午10:01 2018/2/4
 * @Description: 跨域请求采用动作链形式实现过滤
 */
public class CrossOriginProcessor extends AbstractProcessor {
    private static final Log log = Logs.get();
    protected String origin;
    protected String methods;
    protected String headers;
    protected String credentials;

    public CrossOriginProcessor() {
        initData("http://127.0.0.1:8080", "get, post, put, delete, options", "origin, content-type, accept, url, Authorization", "true");
        //http://localhost:63343
    }
    public void initData(String origin, String methods, String headers, String credentials) {
        this.origin = origin;
        this.methods = methods;
        this.headers = headers;
        this.credentials = credentials;
    }

    public void process(ActionContext ac) throws Throwable {
        System.out.println("crossProcessor");
        HttpServletRequest request = ac.getRequest();

        HttpServletResponse resp = ac.getResponse();

        String origin = request.getHeader("Origin");

        if(!Strings.isBlank(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }

        if(!Strings.isBlank(this.methods)) {
            resp.setHeader("Access-Control-Allow-Methods", this.methods);
        }

        if(!Strings.isBlank(this.headers)) {
            resp.setHeader("Access-Control-Allow-Headers", this.headers);
        }

        if(!Strings.isBlank(this.credentials)) {

            resp.setHeader("Access-Control-Allow-Credentials", this.credentials);
        }

        //resp.setHeader("Access-Control-Expose-Headers", "X-Cookie");

        if("OPTIONS".equals(ac.getRequest().getMethod())) {
            if(log.isDebugEnabled()) {
                log.debugf("Feedback -- [%s] [%s] [%s] [%s]", new Object[]{this.origin, this.methods, this.headers, this.credentials});
            }
        } else {
            System.out.println("donNextCross");
            doNext(ac);
        }

    }
}
