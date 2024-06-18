package mg.itu.prom16.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Pour que l'annotation soit disponible à l'exécution
@Target(ElementType.PARAMETER) // Pour limiter l'utilisation de l'annotation aux paramètres de méthode
public @interface RequestParam {
    String value() default ""; 
}

