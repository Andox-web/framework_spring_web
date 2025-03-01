package mg.itu.prom16.caster;

import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.request.ArgumentException;

public abstract class RequestTypeCaster<T> {
    public abstract T[] resolveArray(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException;
    public abstract Collection<T> resolveList(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException;
    public abstract T resolveObject(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException;

    public Object resolve(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException {
        if (type.isArray()) {
            return resolveArray(type, paramName, request);
        } else if (Collection.class.isAssignableFrom(type)) {
            return resolveList(type, paramName, request);
        } else {
            return resolveObject(type, paramName, request);
        }
    }
}
