package mg.itu.prom16.response;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Model extends ResponseHandler {
    void addObject(String name, Object object);
    Map<String, Object> getModel();
}
