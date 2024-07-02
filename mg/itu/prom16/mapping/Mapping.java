package mg.itu.prom16.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.exception.request.MappingNotAllowedException;
import mg.itu.prom16.servlet.Session;
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
    private Object execute(HttpServletRequest request,Object... arg) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Object instance= createInstance(controlleClass,request);
        return method.invoke(instance, arg);
    }
    public Object execute(HttpServletRequest request, HttpServletResponse response) throws MappingNotAllowedException, IllegalArgumentException, ArgumentException, ReflectiveOperationException {
        if (!MappingHandler.isAllowed(this, request, response)) {
            throw new MappingNotAllowedException("Mapping not allowed for current request.");
        }

        return execute(request,ArgumentsResolver.resolveArguments(request, response, this));
    }
    private static Object createInstance(Class<?> controllerClass, HttpServletRequest request) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
        // Obtenir le constructeur par défaut
        Constructor<?> constructor = controllerClass.getDeclaredConstructor();
        // Créer une nouvelle instance du contrôleur
        Object instance = constructor.newInstance();
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
