package com.express.mvc.filter;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.VoidView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 允许跨域
 */
@IocBean(name="crossFilter")
public class CrossOriginsFilter implements ActionFilter {
    private static final Log log = Logs.get();
    protected String origin;
    protected String methods;
    protected String headers;
    protected String credentials;

    public CrossOriginsFilter() {
        this("http://localhost:63343", "get, post, put, delete, options", "origin, content-type, accept, url", "true");
        //http://localhost:63343
    }

    public CrossOriginsFilter(String origin, String methods, String headers, String credentials) {
        this.origin = origin;
        this.methods = methods;
        this.headers = headers;
        this.credentials = credentials;
    }

    public View match(ActionContext ac) {
        System.out.println("crossFilter");
        HttpServletResponse resp = ac.getResponse();
        HttpServletRequest request = ac.getRequest();

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

        resp.setHeader("Access-Control-Expose-Headers", "X-Cookie");

        if("OPTIONS".equals(ac.getRequest().getMethod())) {
            if(log.isDebugEnabled()) {
                log.debugf("Feedback -- [%s] [%s] [%s] [%s]", new Object[]{this.origin, this.methods, this.headers, this.credentials});
            }
            return new VoidView();
        } else {
            return null;
        }
    }
}
