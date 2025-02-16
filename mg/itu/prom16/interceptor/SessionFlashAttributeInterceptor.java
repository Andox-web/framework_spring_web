package mg.itu.prom16.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.response.FlashMap;
import mg.itu.prom16.response.SessionFlashAttribute;
import mg.itu.prom16.mapping.ExecutionResult;
import mg.itu.prom16.response.RedirectAttributes;

public class SessionFlashAttributeInterceptor extends HandlerInterceptor {
    private final SessionFlashAttribute flashAttributeManager;

    public SessionFlashAttributeInterceptor() {
        this.flashAttributeManager = new SessionFlashAttribute();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        flashAttributeManager.retrieveInputFlashMap(request, response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, ExecutionResult executionResult) throws Exception {
        FlashMap combinedFlashMap = new FlashMap();
        
        for (Object arg : executionResult.getArgs()) {
            if (arg instanceof RedirectAttributes) {
                RedirectAttributes redirectAttributes = (RedirectAttributes) arg;
                combinedFlashMap.getAttributes().putAll(redirectAttributes.getFlashMap().getAttributes());
            }
        }
        
        if (!combinedFlashMap.getAttributes().isEmpty()) {
            flashAttributeManager.saveOutputFlashMap(combinedFlashMap, request, response);
        }
    }
}
