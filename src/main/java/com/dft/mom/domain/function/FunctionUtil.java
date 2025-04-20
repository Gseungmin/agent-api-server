package com.dft.mom.domain.function;

import jakarta.servlet.http.HttpServletRequest;

public class FunctionUtil {
    public static String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            } else {
                return ip.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
