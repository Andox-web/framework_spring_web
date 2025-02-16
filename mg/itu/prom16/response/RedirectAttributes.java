package mg.itu.prom16.response;

import java.util.Map;

public interface RedirectAttributes extends Model {
    void addFlashAttribute(String name, Object value);
    Map<String, Object> getFlashAttributes();
    FlashMap getFlashMap();
}
