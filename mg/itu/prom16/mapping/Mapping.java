package mg.itu.prom16.mapping;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.exception.request.MappingNotAllowedException;
import mg.itu.prom16.servlet.Session;
import mg.itu.prom16.util.ArgumentsResolver;
import mg.itu.prom16.util.BeanFactory;
import mg.itu.prom16.validation.ValidationScanner;
import mg.itu.prom16.response.ModelAndView;
import mg.itu.prom16.response.ResponseHandler;
import mg.itu.prom16.response.StringResponseHandler;

public class Mapping {
    Class<?> controlleClass;    
    Method method;
    boolean isRest;
    
    public Mapping(Class<?> controlleClass, Method method) {
        this.controlleClass = controlleClass;
        this.method = method;
        this.isRest=false;
    }
    public Class<?> getControlleClass() {
        return controlleClass;
    }
    public void setControlleClass(Class<?> controlleClass) {
        this.controlleClass = controlleClass;
    }
    public boolean isRest() {
        return isRest;
    }
    public void setRest(boolean isRest) {
        this.isRest = isRest;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }

    
    private ExecutionResult execute(HttpServletRequest request, HttpServletResponse response, Object... arg) 
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, IOException, ServletException {
        Object instance = createInstance(controlleClass, request);
        Object result = method.invoke(instance, arg);

        for (Object argument : arg) {
            if (argument instanceof ResponseHandler responseHandler) {
                responseHandler.handleAction(request, response);
            }
        }
        
        return new ExecutionResult(result, arg, this);
    }

    public ExecutionResult execute(HttpServletRequest request, HttpServletResponse response) 
            throws MappingNotAllowedException, IllegalArgumentException, ArgumentException, ReflectiveOperationException, IOException, ServletException {
        Object[] args = ArgumentsResolver.resolveArguments(request, response, this);
        ValidationScanner validationScanner = new ValidationScanner();
        validationScanner.scanAndValidate(method, args);
        return execute(request, response, args);
    }

    private static Object createInstance(Class<?> controllerClass, HttpServletRequest request) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
        // Obtenir le constructeur par défaut
        Object instance = BeanFactory.getBean(controllerClass);
        if (instance == null) {
            throw new InstantiationException("Impossible de créer une instance de " + controllerClass.getName());
        }
        // Créer une nouvelle instance de Session
        Session session = new Session(request);

        // Parcourir les attributs de la classe pour vérifier s'il y a un attribut de type Session
        for (Field field : controllerClass.getDeclaredFields()) {
            if (field.getType().equals(Session.class)) {
                // Rendre l'attribut accessible si nécessaire
                field.setAccessible(true);

               
                // Injecter l'instance de Session dans le contrôleur
                field.set(instance, session);

                // Si vous ne souhaitez injecter qu'une seule instance de Session, vous pouvez arrêter la boucle ici
                break;
            }
        }

        return instance;
    }

}
