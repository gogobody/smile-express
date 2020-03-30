package com.express.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: WebUtils
 */
public class WebUtils {

    public static boolean isAjax(HttpServletRequest request){
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        return isAjax;
    }

    /**
     * 从request中获取相应得 cookie
     * @param request
     * @param cookieKey
     * @return
     */
    public static String getCookieByRequest(HttpServletRequest request,String cookieKey){
        Cookie[] cookies = request.getCookies();
        for(Cookie c:cookies){
            if(cookieKey.equals(c.getName())){
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * 从request中获取相应的Header
     *
     */
    public static String getHeaderFromRquest(HttpServletRequest request,String headerKey){
        return request.getHeader(headerKey);
    }


    /**
     * 判断请求来自移动端还是web端
     * @param request
     * @return
     */
    public static boolean isMoblie(HttpServletRequest request) {
        boolean isMoblie = false;
        String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi","opera mini","ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",  "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",  "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian","ericsson", "philips", "sagem","wellcom", "bunjalloo", "maui","smartphone", "iemobile", "spice", "bird", "zte-", "longcos","pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac","blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs","kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi","mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port","prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem","smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v","voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-","Googlebot-Mobile" };
        if (request.getHeader("User-Agent") != null) {
            for (String mobileAgent : mobileAgents) {
                if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie;
    }

}
