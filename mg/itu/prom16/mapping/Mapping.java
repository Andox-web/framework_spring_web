package mg.itu.prom16.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.exception.request.MappingNotAllowedException;
import mg.itu.prom16.util.ArgumentsResolver;

public class Mapping {
    Class<?> controlleClass;    
    Method method;
    public Mapping(Class<?> controlleClass, Method method) {
        this.controlleClass = controlleClass;
        this.method = method;
    }
    public Class<?> getControlleClass() {
        return controlleClass;
    }
    public void setControlleClass(Class<?> controlleClass) {
        this.controlleClass = controlleClass;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    private Object execute(Object... arg) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor<?> constructor = controlleClass.getDeclaredConstructor();
        Object instance = constructor.newInstance();
        return method.invoke(instance, arg);
    }
    public Object execute(HttpServletRequest request, HttpServletResponse response) throws MappingNotAllowedException, IllegalArgumentException, ArgumentException, ReflectiveOperationException {
        if (!MappingHandler.isAllowed(this, request, response)) {
            throw new MappingNotAllowedException("Mapping not allowed for current request.");
        }

        return execute(ArgumentsResolver.resolveArguments(request, response, this));
    }
}
