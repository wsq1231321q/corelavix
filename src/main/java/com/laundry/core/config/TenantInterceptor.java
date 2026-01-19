package com.laundry.core.config;

import com.laundry.core.exception.TenantNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID");
        String userId = request.getHeader("X-User-ID");

        if (tenantId == null || tenantId.isEmpty()) {
            throw new TenantNotFoundException("Header X-Tenant-ID es obligatorio");
        }

        TenantContext.setTenantId(Long.parseLong(tenantId));

        if (userId != null && !userId.isEmpty()) {
            TenantContext.setUserId(Long.parseLong(userId));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
