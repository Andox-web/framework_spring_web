package mg.itu.prom16.mapping;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.mapping.GetMapping;
import mg.itu.prom16.annotation.mapping.PostMapping;
import mg.itu.prom16.exception.request.MappingNotAllowedException;

public class MappingHandler {
    // Liste statique des annotations de mapping
    private static final Class<? extends Annotation>[] MAPPING_ANNOTATIONS = new Class[] {GetMapping.class,PostMapping.class};

    public static String getUrl(Method method) {
        for (Class<? extends Annotation> annotation : MAPPING_ANNOTATIONS) {
            if (method.isAnnotationPresent(annotation)) {
                Annotation mappingAnnotation = method.getAnnotation(annotation);
                if (annotation.equals(GetMapping.class)) {
                    return ((GetMapping) mappingAnnotation).url();
                } else if (annotation.equals(PostMapping.class)) {
                    return ((PostMapping) mappingAnnotation).url();
                    // Ajoutez d'autres conditions pour d'autres annotations de mapping si nécessaire
                }
            }
        }
        return null; // Retourne null si aucune annotation de mapping avec URL n'est trouvée
    }
    public static boolean isAllowed(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws MappingNotAllowedException {
        // Vérifier si la méthode est annotée avec GetMapping
        if (mapping.getMethod().isAnnotationPresent(GetMapping.class)) {
            // Exemple de vérification basée sur la requête
            String method = request.getMethod();
            if ("GET".equals(method)) {
                return true;
            } else {
                throw new MappingNotAllowedException("Mapping not allowed for HTTP method: " + method);
            }
        }

        // Vérifier si la méthode est annotée avec PostMapping
        if (mapping.getMethod().isAnnotationPresent(PostMapping.class)) {
            // Exemple de vérification basée sur la requête
            String method = request.getMethod();
            if ("POST".equals(method)) {
                return true;
            } else {
                throw new MappingNotAllowedException("Mapping not allowed for HTTP method: " + method);
            }
        }

        // Si aucune des conditions n'est remplie, retourner false ou lancer une exception selon le cas
        throw new MappingNotAllowedException("Mapping not allowed.");
    }

}
    
