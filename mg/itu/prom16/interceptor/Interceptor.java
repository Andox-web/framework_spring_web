package mg.itu.prom16.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.mapping.ExecutionResult;

public interface Interceptor {
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
    void postHandle(HttpServletRequest request, HttpServletResponse response,ExecutionResult executionResult) throws Exception;
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception;
}
