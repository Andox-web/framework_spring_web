package mg.itu.prom16.interceptor;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.role.RoleSessionManager;
import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.exception.UnauthorizedException;
import mg.itu.prom16.response.FlashMap;
import mg.itu.prom16.response.SessionFlashAttribute;

@Component
public class RoleInterceptor extends HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof Method method) {
            RoleSessionManager roleSessionManager = new RoleSessionManager(request);
            try {
                return roleSessionManager.checkRole(method);
            } catch (UnauthorizedException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String url = e.getAnnotationUnauthorizedRedirectUrl();
                if (url == null || url.isEmpty()) {
                    url = roleSessionManager.getUnauthorizedRedirectUrl();
                }
                if (url != null && !url.isEmpty()) {
                    FlashMap map = new FlashMap();
                    map.put("error", e.getMessage());
                    new SessionFlashAttribute().saveOutputFlashMap(map, request, response);
                    response.sendRedirect(request.getContextPath() + url);	
                }
                return false;
            }
        }
        return true;
    }
}
