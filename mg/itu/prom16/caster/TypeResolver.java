package mg.itu.prom16.caster;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.exception.request.ArgumentException;

public class TypeResolver extends RequestTypeCaster<Object> {

    private static final Map<Class<?>, Function<String, ?>> typeParsers = new HashMap<>();

    static {
        typeParsers.put(String.class, Function.identity());
        typeParsers.put(Integer.class, Integer::parseInt);
        typeParsers.put(int.class, Integer::parseInt);
        typeParsers.put(Long.class, Long::parseLong);
        typeParsers.put(long.class, Long::parseLong);
        typeParsers.put(Float.class, Float::parseFloat);
        typeParsers.put(float.class, Float::parseFloat);
        typeParsers.put(Double.class, Double::parseDouble);
        typeParsers.put(double.class, Double::parseDouble);
        typeParsers.put(Boolean.class, TypeResolver::parseBoolean);
        typeParsers.put(boolean.class, TypeResolver::parseBoolean);
        typeParsers.put(Character.class, TypeResolver::parseChar);
        typeParsers.put(char.class, TypeResolver::parseChar);
        typeParsers.put(Byte.class, Byte::parseByte);
        typeParsers.put(byte.class, Byte::parseByte);
        typeParsers.put(Short.class, Short::parseShort);
        typeParsers.put(short.class, Short::parseShort);
        typeParsers.put(BigDecimal.class, BigDecimal::new);
        typeParsers.put(BigInteger.class, BigInteger::new);
        typeParsers.put(Date.class, TypeResolver::parseDate);
        typeParsers.put(LocalDate.class, LocalDate::parse);
        typeParsers.put(LocalDateTime.class, LocalDateTime::parse);
        typeParsers.put(UUID.class, UUID::fromString);
    }

    public static Object castValue(String paramValue, Class<?> paramType) throws ArgumentException {
        if (paramValue == null) {
            return null;
        }
        Function<String, ?> parser = typeParsers.get(paramType);
        if (parser != null) {
            try {
                return parser.apply(paramValue);
            } catch (Exception e) {
                throw new ArgumentException("Erreur de conversion pour le type " + paramType.getSimpleName() + " : " + e.getMessage(), e);
            }
        }
        throw new ArgumentException("Type de paramètre non pris en charge : " + paramType.getSimpleName());
    }

    private static boolean parseBoolean(String paramValue) {
        if ("on".equalsIgnoreCase(paramValue)) {
            return true;
        }
        return Boolean.parseBoolean(paramValue);
    }

    private static char parseChar(String paramValue) {
        return paramValue.charAt(0);
    }

    private static Date parseDate(String paramValue) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(paramValue);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Object[] resolveArray(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException {
        String[] paramValues = request.getParameterValues(paramName);
        if (paramValues == null) {
            return new Object[0];
        }
        Object[] result = new Object[paramValues.length];
        for (int i = 0; i < paramValues.length; i++) {
            result[i] = castValue(paramValues[i], type);
        }
        return result;
    }

    @Override
    public Collection<Object> resolveList(Class<?> type, String paramName, HttpServletRequest request)
            throws ArgumentException {
        String[] paramValues = request.getParameterValues(paramName);
        if (paramValues == null) {
            return new ArrayList<>();
        }
        List<Object> result = new ArrayList<>();
        for (String paramValue : paramValues) {
            result.add(castValue(paramValue, type));
        }
        return result;
    }

    @Override
    public Object resolveObject(Class<?> type, String paramName, HttpServletRequest request) throws ArgumentException {
        String paramValue = request.getParameter(paramName);
        if (paramValue == null) {
            return null;
        }
        return castValue(paramValue, type);
    }
    
}
