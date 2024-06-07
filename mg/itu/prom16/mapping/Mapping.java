package mg.itu.prom16.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public Object execute(Object... arg) throws IllegalAccessException, InvocationTargetException, InstantiationException{
        
        return method.invoke(controlleClass.newInstance(), arg);
    }    
}
