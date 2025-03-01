package mg.itu.prom16.response;

import java.util.Map;

public interface RedirectAttributes extends Model {
    RedirectAttributes addFlashAttribute(String name, Object value);
    Map<String, Object> getFlashAttributes();
    FlashMap getFlashMap();
}
