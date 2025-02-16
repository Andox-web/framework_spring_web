package mg.itu.prom16.interceptor;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.role.RoleSessionManager;

public class RoleInterceptor extends HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof Method method) {
            RoleSessionManager roleSessionManager = new RoleSessionManager(request);
            return roleSessionManager.checkRole(method,response);
        }
        return true;
    }
}
