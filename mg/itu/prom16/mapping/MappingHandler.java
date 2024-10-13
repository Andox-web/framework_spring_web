package mg.itu.prom16.mapping;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.mapping.GetMapping;
import mg.itu.prom16.annotation.mapping.PostMapping;
import mg.itu.prom16.annotation.mapping.Url;
import mg.itu.prom16.exception.request.MappingNotAllowedException;

public class MappingHandler {
    // Liste statique des annotations de mapping
    private static final Class<? extends Annotation> URL_ANNOTATIONS = Url.class;
    public static VerbMapping getUrl(Method method) {
        if (method.isAnnotationPresent(URL_ANNOTATIONS)) {
            return VerbMapping.getVerbMapping(method,URL_ANNOTATIONS);
        }   
        return null; // Retourne null si aucune annotation de mapping avec URL n'est trouvée
    }

}
    
