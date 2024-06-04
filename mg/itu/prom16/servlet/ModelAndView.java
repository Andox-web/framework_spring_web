package mg.itu.prom16.servlet;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private final Map<String, Object> model;
    private final String viewName;

    public ModelAndView(String viewName) {
        this.model = new HashMap<>();
        this.viewName = viewName;
    }

    public void addObject(String name, Object object) {
        model.put(name, object);
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getViewName() {
        return viewName;
    }
}
