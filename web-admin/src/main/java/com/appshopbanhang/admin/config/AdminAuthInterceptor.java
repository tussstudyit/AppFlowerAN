package com.appshopbanhang.admin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object adminUsername = request.getSession().getAttribute(AdminSession.ADMIN_USERNAME);
        if (adminUsername != null) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/admin/login");
        return false;
    }
}
