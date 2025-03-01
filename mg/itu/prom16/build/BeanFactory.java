package mg.itu.prom16.build;

import java.util.HashMap;
import java.util.Map;

import mg.itu.prom16.annotation.Bean;
import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.annotation.Configuration;
import mg.itu.prom16.util.AnnotationUtils;
import mg.itu.prom16.util.BeanInjector;
import mg.itu.prom16.util.GenericTypeUtils;
import mg.itu.prom16.util.ValueInjector;

public class BeanFactory {

    private static final Map<Class<?>, Object> beans = new HashMap<>();
    
    public static <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(beans.get(beanClass));
    }

    public static Map<Class<?>, Object> getBeans() {
        return beans;
    }

    public final static void createBeans() throws Exception {
        Map<Class<?>, Object> tempBeans = new HashMap<>();
        Map<Class<?>, BeanInstantiator<?>> beanInstantiators = new HashMap<>();

        // Instantiate configurations first
        for (Class<?> configClass : PackageScanner.getCLASSES()) {
            if (AnnotationUtils.hasAnnotation(configClass, Configuration.class)) {
                Object instance = configClass.getDeclaredConstructor().newInstance();
                tempBeans.put(configClass, instance);
                if (BeanInstantiator.class.isAssignableFrom(configClass)) {
                    BeanInstantiator<?> instantiator = (BeanInstantiator<?>) instance;
                    Class<?> type = (Class<?>)GenericTypeUtils.getGenericTypes(configClass)[0];
                    beanInstantiators.put(type, instantiator);
                }
            }
        }

        // Inject values into configurations
        for (Map.Entry<Class<?>, Object> entry : tempBeans.entrySet()) {
            ValueInjector.injectValues(entry.getValue());
        }

        // Instantiate components
        for (Class<?> componentClass : PackageScanner.getCLASSES()) {
            if (AnnotationUtils.hasAnnotation(componentClass, Component.class)) {
                Object instance = null;
                BeanInstantiator<?> instantiator = beanInstantiators.get(componentClass);
                if (instantiator != null) {
                    instance = instantiator.instantiate();
                }
                else{
                    instance = componentClass.getDeclaredConstructor().newInstance();
                }
                tempBeans.put(componentClass, instance);
            }
        }

        // Inject values and put them in beans
        for (Map.Entry<Class<?>, Object> entry : tempBeans.entrySet()) {
            ValueInjector.injectValues(entry.getValue());
            if (AnnotationUtils.hasAnnotation(entry.getKey(), Component.class)) {
                beans.put(entry.getKey(), entry.getValue());
            }
        }

        // Handle @Bean methods
        for (Class<?> configClass : PackageScanner.getCLASSES()) {
            if (AnnotationUtils.hasAnnotation(configClass, Configuration.class)) {
                Object configInstance = tempBeans.get(configClass);
                for (var method : configClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Object bean = null;
                        try {
                            bean = method.invoke(configInstance);
                        } catch (Exception e) {
                            if(e.getCause() instanceof Exception exception){
                                throw exception;
                            }
                            throw new RuntimeException(e.getCause());
                        }
                        if (bean!=null) {
                            ValueInjector.injectValues(bean);
                        }
                        beans.put(method.getReturnType(), bean);
                    }
                }
            }
        }

        // Inject beans into attributes with @Autowired annotation
        for (Object bean : beans.values()) {
            BeanInjector.injectBeans(bean, beans);
        }
    }
}
