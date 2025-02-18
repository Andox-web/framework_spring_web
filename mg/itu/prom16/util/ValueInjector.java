package mg.itu.prom16.util;

import java.lang.reflect.Field;
import mg.itu.prom16.annotation.Value;

public class ValueInjector {

    public static void injectValues(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                if (value.startsWith("${") && value.endsWith("}")) {
                    String key = value.substring(2, value.length() - 1);
                    value = Environment.getProperty(key);
                }
                Object castedValue = TypeResolver.castValue(value, field.getType());
                field.setAccessible(true);
                field.set(instance, castedValue);
            }
        }
    }
}
