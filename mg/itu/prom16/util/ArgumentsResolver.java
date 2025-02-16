package mg.itu.prom16.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mg.itu.prom16.annotation.param.RequestBody;
import mg.itu.prom16.annotation.param.RequestParam;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.servlet.MultipartFile;
import mg.itu.prom16.servlet.Session;
import mg.itu.prom16.validation.BindingResult;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.ModelMap;
import mg.itu.prom16.response.RedirectAttributes;
import mg.itu.prom16.response.RedirectAttributesMap;

public class ArgumentsResolver {

    private static final Class<? extends Annotation>[] PARAMETER_ANNOTATIONS = new Class[]{RequestParam.class, RequestBody.class};

    public static Object[] resolveArguments(HttpServletRequest request, HttpServletResponse response, Mapping mapping) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException, IOException, ServletException {
        
        List<Object> args = new ArrayList<>();
        
        // Récupérer les paramètres de la méthode associée au mapping
        Parameter[] parameters = mapping.getMethod().getParameters();

        for (Parameter parameter : parameters) {
            // Vérifier si le paramètre est annoté avec une des annotations de PARAMETER_ANNOTATIONS
            Object arg = resolveCustomArgument(parameter, mapping.getMethod(), request, response);
            if (arg == null) {
                throw new ArgumentException("Paramètre non géré : " + parameter.getName());
            }
            args.add(arg);
        }

        return args.toArray(new Object[0]);
    }

    private static Object resolveCustomArgument(Parameter parameter, Object method, HttpServletRequest request, HttpServletResponse response) throws IllegalArgumentException, ArgumentException, ReflectiveOperationException, IOException, ServletException {
        Class<?> type = parameter.getType();

        if (type.equals(BindingResult.class)) {
            return new BindingResult();
        }
        if (type.equals(Session.class)) {
            return new Session(request);
        }
        if (type.equals(HttpServletRequest.class)) {
            return request;
        }
        if (type.equals(HttpServletResponse.class)) {
            return response;
        }
        if (RedirectAttributes.class.isAssignableFrom(type)) {
            return new RedirectAttributesMap();
        }
        if (Model.class.isAssignableFrom(type)) {
            return new ModelMap();
        }

        for (Class<? extends Annotation> annotationClass : PARAMETER_ANNOTATIONS) {
            if (parameter.isAnnotationPresent(annotationClass)) {
                Annotation annotation = parameter.getAnnotation(annotationClass);
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    if (paramName.isEmpty()) {
                        paramName = parameter.getName();
                    }

                    if (type.equals(MultipartFile.class)) {
                        Part part = request.getPart(paramName);
                        if (part == null) {
                            return null;
                        }
                        return new MultipartFile(part);
                    }

                    String paramValue = request.getParameter(paramName);

                    if (paramValue == null) {
                        return null;
                    }

                    return TypeResolver.castValue(paramValue, type);
                } else if (annotation instanceof RequestBody) {
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
            }
        }

        throw new ArgumentException("ETU002624 argument non gerer (non annote)");
    }
}

