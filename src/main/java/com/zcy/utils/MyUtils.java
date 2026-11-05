package com.zcy.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义工具类
 */
public class MyUtils {
    
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }
    
    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }
    
    /**
     * 获取客户端IP地址
     */
    public static String getIpAddr(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}