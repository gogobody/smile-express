package com.express.mvc.filter;
import com.express.domain.User;
import com.express.mvc.context.UserContext;
import com.express.security.JwtToken;
import com.express.utils.WebUtils;
import com.express.web.support.ResponseResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.VoidView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @Author :gogobody
 * @Date Created in 下午12:09 2018/2/4
 * @Description:
 */
@IocBean(name = "tokenFilter")
public class AccessTokenFilter implements ActionFilter {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenFilter.class);

    public View match(ActionContext actionContext) {
        System.out.println("AccessTokenFilter");
        HttpServletResponse response = actionContext.getResponse();
        HttpServletRequest request = actionContext.getRequest();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        //response.setCharacterEncoding("UTF-8");
        try {
           // request.setCharacterEncoding("UTF-8");
            if(!checkToken(request,response)) return new VoidView();
        } catch (Exception e) {
            Object result = ResponseResult.newFailResult(e.getMessage());
            try {
                response.getWriter().write(Json.toJson(result));
            } catch (IOException e1) {
                throw new RuntimeException(e1.getMessage());
                //e1.printStackTrace();
            }
            return new VoidView();
        }

        return null;
    }

    private boolean checkToken(HttpServletRequest request , HttpServletResponse response) throws IOException {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("-Xrunjdwp") || arg.startsWith("-agentlib:jdwp")) {
                return true;
            }
        }
        String authorization = WebUtils.getHeaderFromRquest(request, "Authorization");
        System.out.println(authorization);
        if (authorization == null) {
            response.getWriter().write(Json.toJson(ResponseResult.newFailResult("未登录", ResponseResult.NO_LOGIN)));
            return false;
        }
        try {
            Claims claims = JwtToken.parseToken(authorization);
            User user = new User();
            user.setName((String) claims.get("name"));
            user.setPassword((String) claims.get("password"));
            user.setId( Long.parseLong(claims.get("id").toString()));
            System.out.println("fff:"+ user.toString());
            UserContext.getCurrentuser().set(user);
            return true;
        } catch (Exception e) {


            // logger.error(e.getMessage(), e);
            if (e instanceof ExpiredJwtException) { //签名过期
                response.getWriter().write(Json.toJson(ResponseResult.newFailResult("expired login", ResponseResult.LOGIN_EXPIRED)));
                System.out.println("ExpiredJwtException");
            } else {
                response.getWriter().write(Json.toJson(ResponseResult.newFailResult("未登录", ResponseResult.NO_LOGIN)));
                System.out.println("Other JwtException");
            }
            return false;
        }
    }
}
