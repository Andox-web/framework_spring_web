package mg.itu.prom16.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.param.RequestBody;
import mg.itu.prom16.annotation.param.RequestParam;
import mg.itu.prom16.caster.RequestTypeCaster;
import mg.itu.prom16.caster.TypeResolver;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.response.Model;
import mg.itu.prom16.response.ModelMap;
import mg.itu.prom16.response.RedirectAttributes;
import mg.itu.prom16.response.RedirectAttributesMap;
import mg.itu.prom16.servlet.Session;
import mg.itu.prom16.validation.BindingResult;

public class ArgumentsResolver {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] PARAMETER_ANNOTATIONS = new Class[]{RequestParam.class, RequestBody.class};

    private static final TypeResolver TYPE_RESOLVER = new TypeResolver();

    private static Map<Class<?>, RequestTypeCaster<?>> CUSTOM_TYPE_CASTERS;
    
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
                return resolve(parameter,parameter.getAnnotation(annotationClass) , request);
            }
        }

        throw new ArgumentException("ETU002624 argument non gerer (non annote)");
    }

    public static Map<Class<?> , RequestTypeCaster<?>> getCustomTypeCasters() {
        if (CUSTOM_TYPE_CASTERS!=null) {
            return CUSTOM_TYPE_CASTERS;
        }
        Map<Class<?> , RequestTypeCaster<?>> customTypeCasters = new HashMap<>();
        for (Object bean : BeanFactory.getBeans().values()) {
            if (bean instanceof RequestTypeCaster requestTypeCaster) {
                Type type = GenericTypeUtils.getGenericTypes(requestTypeCaster.getClass())[0];
                if (type instanceof Class<?> clazz) {
                    customTypeCasters.put(clazz, requestTypeCaster);
                }
            }
        }
        CUSTOM_TYPE_CASTERS =  customTypeCasters;
        return customTypeCasters;
    }

    private static Object resolveField(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException {
        if (getCustomTypeCasters().containsKey(type)) {
            return getCustomTypeCasters().get(type).resolve(type, paramName, request);
        }
        return TYPE_RESOLVER.resolve(type, paramName, request);
    }

    public static Object resolve(Parameter parameter, Annotation annotation, HttpServletRequest request) throws ArgumentException {
        Class<?> type = parameter.getType();

        if (annotation instanceof RequestParam requestParam) {
            String paramName = requestParam.value();
            if (paramName.isEmpty()) {
                paramName = parameter.getName();
            }

            return resolveField(type, paramName, request);

        } else if (annotation instanceof RequestBody requestBody) {
            try {
                String paramName = requestBody.value();
                if (paramName.isEmpty()) {
                    paramName = parameter.getName();
                }
                
                if (requestBody.isJsonable() && request.getParameter(paramName) != null) {
                    return new Gson().fromJson(request.getParameter(paramName), type);
                }

                Constructor<?> constructor = parameter.getType().getDeclaredConstructor();
                Object obj = constructor.newInstance();
                
                for (Field field : obj.getClass().getDeclaredFields()) {
                    String fieldName = field.getName();
                    String[] paramValues = request.getParameterValues(fieldName);
                    if (paramValues != null) {
                        field.setAccessible(true);
                        field.set(obj, resolveField(field.getType(), fieldName, request));
                    }
                }

                return obj;
            } catch (Exception e) {
                throw new ArgumentException(e);
            }
        }

        return null;
    }
}

