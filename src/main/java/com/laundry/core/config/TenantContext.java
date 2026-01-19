package com.laundry.core.config;

public class TenantContext {
    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentUser = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
        currentUser.remove();
    }

    public static void setUserId(Long userId) {
        currentUser.set(userId);
    }

    public static Long getUserId() {
        return currentUser.get();
    }
}
