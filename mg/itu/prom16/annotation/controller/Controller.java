package mg.itu.prom16.annotation.controller;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
public @interface Controller {
    String name() default "";
}
