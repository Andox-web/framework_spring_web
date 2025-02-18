package mg.itu.prom16.util;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtils {

    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        // Si la classe est une annotation, retourne false
        if (clazz.isAnnotation()) {
            return false;
        }
        return hasAnnotationRecursively(clazz, annotationClass, new HashSet<>());
    }

    private static boolean hasAnnotationRecursively(Class<?> clazz, Class<? extends Annotation> annotationClass, Set<Class<?>> visited) {
        // Empêche les boucles infinies en évitant de revisiter une annotation déjà vérifiée
        if (!visited.add(clazz)) {
            return false;
        }

        // Vérifie si l'annotation est directement présente sur la classe
        if (clazz.isAnnotationPresent(annotationClass)) {
            return true;
        }

        // Vérifie récursivement si une annotation présente sur la classe contient elle-même annotationClass
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (hasAnnotationRecursively(annotationType, annotationClass, visited)) {
                return true;
            }
        }

        return false;
    }

}
