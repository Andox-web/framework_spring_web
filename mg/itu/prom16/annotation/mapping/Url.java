package mg.itu.prom16.annotation.mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
public  @interface Url {
    String value() default "";
}