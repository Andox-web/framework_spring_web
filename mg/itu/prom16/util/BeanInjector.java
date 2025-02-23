package mg.itu.prom16.util;

import java.lang.reflect.Field;
import java.util.Map;

import mg.itu.prom16.annotation.Autowired;

public class BeanInjector {

    public static void injectBeans(Object instance, Map<Class<?>, Object> beans) throws Exception {
        if (instance == null) {
            return;
        }
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                Object bean = beans.get(fieldType);
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }
        }
    }
}
