package mg.itu.prom16.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.param.RequestBody;
import mg.itu.prom16.annotation.param.RequestParam;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.mapping.Mapping;

public class ArgumentsResolver {

    private static final Object[] PARAMETER_ANNOTATIONS = {RequestParam.class,RequestBody.class};

    public static Object[] resolveArguments(HttpServletRequest request, HttpServletResponse response, Mapping mapping) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException {
        List<Object> args = new ArrayList<>();

        // Récupérer les paramètres de la méthode associée au mapping
        Parameter[] parameters = mapping.getMethod().getParameters();

        for (Parameter parameter : parameters) {
            // Vérifier si le paramètre est annoté avec une des annotations de PARAMETER_ANNOTATIONS
            Annotation annotation = findParameterAnnotation(parameter);
            if (annotation != null) {

                Object arg = resolveCustomArgument(parameter, annotation, request, response);
                if (arg == null) {
                    throw new ArgumentException("Paramètre non géré : " + parameter.getName());
                }
                args.add(arg);
            }
        }
        

        return args.toArray(new Object[0]);
    }

    // Méthode pour rechercher une annotation sur un paramètre
    private static  Annotation findParameterAnnotation(Parameter parameter) {
        for (Object annotationClass : PARAMETER_ANNOTATIONS) {
            Annotation annotation = parameter.getAnnotation((Class<? extends Annotation>) annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    // Exemple de résolution d'un paramètre personnalisé avec annotation
    private static Object resolveCustomArgument(Parameter parameter, Annotation annotation, HttpServletRequest request, HttpServletResponse response) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException {
        if (annotation instanceof RequestParam) {
            String paramName = ((RequestParam) annotation).value();
            if (paramName.isEmpty()) {
                paramName = parameter.getName();
            }
            String paramValue = request.getParameter(paramName);
            return TypeResolver.castValue(paramValue,parameter.getType());
        }else if (annotation instanceof RequestBody) {
            try {
                
                Constructor<?> constructor = parameter.getType().getDeclaredConstructor();
                Object obj = constructor.newInstance();
                
                for (Field field : obj.getClass().getDeclaredFields()) {
                    String fieldName = field.getName();
                    String paramValue = request.getParameter(fieldName);
                    if (paramValue != null) {
                        field.setAccessible(true);
                        field.set(obj, TypeResolver.castValue(paramValue, field.getType()));
                    }
                }

                return obj;
            } catch (InstantiationException | IllegalAccessException e) {
               throw new ArgumentException(e);
            }
        }

        throw new ArgumentException("argument non gerer (non annote)");
    }
}

