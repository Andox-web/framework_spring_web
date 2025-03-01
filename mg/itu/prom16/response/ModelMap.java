package mg.itu.prom16.response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ModelMap implements Model {
    private final Map<String, Object> model;

    public ModelMap() {
        this.model = new HashMap<>();
    }

    @Override
    public Model addObject(String name, Object object) {
        model.put(name, object);
        return this;
    }

    @Override
    public Map<String, Object> getModel() {
        return model;
    }

    @Override
    public Object getGsonnableResponse() {
        return getModel();
    }

    @Override
    public void processAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleAction(request, response);
    }

    @Override
    public void handleAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        for (Entry<String, Object> keyValueEntry : getModel().entrySet()) {
            request.setAttribute(keyValueEntry.getKey(), keyValueEntry.getValue());
        }
    }
}
