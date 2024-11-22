package mg.itu.prom16.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mg.itu.prom16.annotation.mapping.Rest;
import mg.itu.prom16.annotation.param.RequestBody;
import mg.itu.prom16.annotation.param.RequestParam;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.servlet.MultipartFile;
import mg.itu.prom16.validation.BindingResult;

public class ArgumentsResolver {

    private static final Object[] PARAMETER_ANNOTATIONS = {RequestParam.class,RequestBody.class};

    public static Object[] resolveArguments(HttpServletRequest request, HttpServletResponse response, Mapping mapping) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException, IOException, ServletException {
        
        List<Object> args = new ArrayList<>();
        
        // Récupérer les paramètres de la méthode associée au mapping
        Parameter[] parameters = mapping.getMethod().getParameters();

        for (Parameter parameter : parameters) {
            // Vérifier si le paramètre est annoté avec une des annotations de PARAMETER_ANNOTATIONS
            Annotation annotation = findParameterAnnotation(parameter);
            Object arg = resolveCustomArgument(parameter, annotation, request, response);
            if (arg == null) {
                throw new ArgumentException("Paramètre non géré : " + parameter.getName());
            }
            args.add(arg);
            
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
    private static Object resolveCustomArgument(Parameter parameter, Annotation annotation, HttpServletRequest request, HttpServletResponse response) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException, IOException, ServletException {
        Class<?> type = parameter.getType();

        if(type.equals(BindingResult.class)){
            return new BindingResult();
        }
        if (annotation instanceof RequestParam) {
            String paramName = ((RequestParam) annotation).value();
            if (paramName.isEmpty()) {
                paramName = parameter.getName();
            }

            
            if (type.equals(MultipartFile.class)) {
                Part part = request.getPart(paramName);
                if (part==null) {
                    return null;
                }
                return new MultipartFile(part); 
            }

            String paramValue = request.getParameter(paramName);

            if (paramValue == null) {
                return null;
            }

            return TypeResolver.castValue(paramValue, type);
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

        throw new ArgumentException("ETU002624 argument non gerer (non annote)");
    }
}

