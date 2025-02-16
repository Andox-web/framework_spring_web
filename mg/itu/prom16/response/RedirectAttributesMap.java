package mg.itu.prom16.response;

import java.util.HashMap;
import java.util.Map;

public class RedirectAttributesMap extends ModelMap implements RedirectAttributes {
    private final Map<String, Object> flashAttributes;
    private FlashMap flashMap;

    public RedirectAttributesMap() {
        this.flashAttributes = new HashMap<>();
        this.flashMap = new FlashMap();
    }

    @Override
    public Object getGsonnableResponse() {
        return flashMap.getAttributes();
    }

    @Override
    public void addFlashAttribute(String name, Object value) {
        flashMap.put(name, value);
    }

    @Override
    public Map<String, Object> getFlashAttributes() {
        return flashAttributes;
    }
    
    @Override
    public FlashMap getFlashMap() {
        return flashMap;
    }
}
