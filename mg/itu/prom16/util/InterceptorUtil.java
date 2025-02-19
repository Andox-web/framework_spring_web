package mg.itu.prom16.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mg.itu.prom16.interceptor.Interceptor;

public class InterceptorUtil {

    public static Interceptor[] getInterceptors() throws ClassNotFoundException, IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        
        Set<Map.Entry<Class<?>, Object>> entries = BeanFactory.getBeans().entrySet();
        for (Map.Entry<Class<?>, Object> entry : entries) {
            if (Interceptor.class.isAssignableFrom(entry.getKey())) {
                interceptors.add((Interceptor) entry.getValue());
            }
        }
        
        return interceptors.toArray(new Interceptor[0]);
    }
}
