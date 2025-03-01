package mg.itu.prom16.mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
public  @interface Url {
    String value() default "";
}