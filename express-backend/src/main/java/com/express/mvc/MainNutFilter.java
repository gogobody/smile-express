package com.express.mvc;

import org.nutz.mvc.NutFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @description: 重写
 */
public class  MainNutFilter extends NutFilter {


    protected Set<String> prefixs = new HashSet<String>();


    @Override
    public void init(FilterConfig conf) throws ServletException {
        super.init(conf);
        prefixs.add(conf.getServletContext().getContextPath() + "/druid/");
        prefixs.add(conf.getServletContext().getContextPath() + "/rs/");
        //prefixs.add(conf.getServletContext().getContextPath() + "/bgo/login");
        /*可以省去
        <init-param>
          <param-name>exclusions</param-name>
          <param-value>/rs/*,/druid/*</param-value>
        </init-param>
        */

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        if (req instanceof HttpServletRequest) {
            String uri = ((HttpServletRequest) req).getRequestURI();
            for (String prefix : prefixs) {
                if (uri.startsWith(prefix)) {
                    System.out.println("chain:::"+uri);
                    chain.doFilter(req, resp);
                    return;
                } /*else {
                    System.out.println(uri);
                    try {
                        if(checkToken((HttpServletRequest)req,(HttpServletResponse)resp)){
                            chain.doFilter(req, resp);
                            return;
                        };
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
        }
        super.doFilter(req, resp, chain);
    }


}
