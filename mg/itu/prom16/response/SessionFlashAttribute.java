package mg.itu.prom16.response;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.itu.prom16.environment.Environment;

public class SessionFlashAttribute implements FlashMapManager {
    private static final String DEFAULT_FLASH_MAP_SESSION_ATTRIBUTE = "flashDataAttribute";

    @Override
    public void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String flashMapSessionAttribute = getFlashMapSessionAttribute(request);
        session.setAttribute(flashMapSessionAttribute, flashMap);
    }

    @Override
    public FlashMap retrieveInputFlashMap(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String flashMapSessionAttribute = getFlashMapSessionAttribute(request);
        FlashMap flashMap = (FlashMap) session.getAttribute(flashMapSessionAttribute);
        session.removeAttribute(flashMapSessionAttribute);

        if (flashMap != null) {
            for (Map.Entry<String, Object> entry : flashMap.getAttributes().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        return flashMap;
    }

    private String getFlashMapSessionAttribute(HttpServletRequest request) {
        return Environment.getProperty("flashMapSession", DEFAULT_FLASH_MAP_SESSION_ATTRIBUTE);
    }
}
