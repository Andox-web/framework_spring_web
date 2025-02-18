package mg.itu.prom16.util;

import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.interceptor.Interceptor;
import mg.itu.prom16.interceptor.RoleInterceptor;
import mg.itu.prom16.interceptor.SessionFlashAttributeInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InterceptorUtil {
    private static final Interceptor[] ALWAYS_PRESENT_INTERCEPTORS = {
        new RoleInterceptor(),
        new SessionFlashAttributeInterceptor()
    };

    public static Interceptor[] getInterceptors() throws ClassNotFoundException, IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        
        // for (Interceptor interceptor : ALWAYS_PRESENT_INTERCEPTORS) {
        //     interceptors.add(interceptor);
        // }
        
        Set<Map.Entry<Class<?>, Object>> entries = BeanFactory.getBeans().entrySet();
        for (Map.Entry<Class<?>, Object> entry : entries) {
            if (Interceptor.class.isAssignableFrom(entry.getKey())) {
                interceptors.add((Interceptor) entry.getValue());
            }
        }
        
        return interceptors.toArray(new Interceptor[0]);
    }
}
