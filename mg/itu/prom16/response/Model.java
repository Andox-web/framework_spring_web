package mg.itu.prom16.response;

import java.util.Map;

public interface Model extends ResponseHandler {
    void addObject(String name, Object object);
    Map<String, Object> getModel();
}
