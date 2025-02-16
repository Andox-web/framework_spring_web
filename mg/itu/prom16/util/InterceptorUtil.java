package mg.itu.prom16.util;

import mg.itu.prom16.interceptor.Interceptor;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;

public class InterceptorUtil {
    public static Interceptor[] getInterceptors() {
        ServiceLoader<Interceptor> loader = ServiceLoader.load(Interceptor.class);
        List<Interceptor> interceptors = new ArrayList<>();
        for (Interceptor interceptor : loader) {
            interceptors.add(interceptor);
        }
        return interceptors.toArray(new Interceptor[0]);
    }
}
