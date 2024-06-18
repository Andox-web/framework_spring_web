package mg.itu.prom16.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import mg.itu.prom16.exception.request.ArgumentException;

public class TypeResolver {

    public static Object castValue(String paramValue, Class<?> paramType) throws ArgumentException{
        try {
            if (paramType.equals(String.class)) {
                return paramValue;
            } else if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
                return parseInt(paramValue);
            } else if (paramType.equals(long.class) || paramType.equals(Long.class)) {
                return parseLong(paramValue);
            } else if (paramType.equals(float.class) || paramType.equals(Float.class)) {
                return parseFloat(paramValue);
            } else if (paramType.equals(double.class) || paramType.equals(Double.class)) {
                return parseDouble(paramValue);
            } else if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
                return parseBoolean(paramValue);
            } else if (paramType.equals(char.class) || paramType.equals(Character.class)) {
                return parseChar(paramValue);
            } else if (paramType.equals(byte.class) || paramType.equals(Byte.class)) {
                return parseByte(paramValue);
            } else if (paramType.equals(short.class) || paramType.equals(Short.class)) {
                return parseShort(paramValue);
            } else if (paramType.equals(BigDecimal.class)) {
                return parseBigDecimal(paramValue);
            } else if (paramType.equals(BigInteger.class)) {
                return parseBigInteger(paramValue);
            } else if (paramType.equals(Date.class)) {
                return parseDate(paramValue);
            }else if (paramType.equals(LocalDate.class)) {
                return parseLocalDate(paramValue);
            } else if (paramType.equals(LocalDateTime.class)) {
                return parseLocalDateTime(paramValue);
            }
            
            // Ajouter d'autres conversions pour d'autres types de paramètres si nécessaire

            throw new ArgumentException("Type de paramètre non pris en charge : " + paramType.getSimpleName());
        } catch (Exception e) {
            throw new ArgumentException("Erreur de conversion pour le type " + paramType.getSimpleName() + " : " + e.getMessage(), e);
        }
    }

    private static int parseInt(String paramValue) throws NumberFormatException {
        return Integer.parseInt(paramValue);
    }

    private static long parseLong(String paramValue) throws NumberFormatException {
        return Long.parseLong(paramValue);
    }

    private static float parseFloat(String paramValue) throws NumberFormatException {
        return Float.parseFloat(paramValue);
    }

    private static double parseDouble(String paramValue) throws NumberFormatException {
        return Double.parseDouble(paramValue);
    }

    private static boolean parseBoolean(String paramValue) {
        // Si la valeur est "on", retourne true
        if ("on".equalsIgnoreCase(paramValue)) {
            return true;
        }
        // Sinon, utilise la conversion par défaut de la chaîne en boolean
        return Boolean.parseBoolean(paramValue);
    }

    private static char parseChar(String paramValue) {
        // Simplification : renvoie le premier caractère de la chaîne
        return paramValue.charAt(0);
    }

    private static byte parseByte(String paramValue) throws NumberFormatException {
        return Byte.parseByte(paramValue);
    }

    private static short parseShort(String paramValue) throws NumberFormatException {
        return Short.parseShort(paramValue);
    }

    private static BigDecimal parseBigDecimal(String paramValue) throws NumberFormatException {
        return new BigDecimal(paramValue);
    }

    private static BigInteger parseBigInteger(String paramValue) throws NumberFormatException {
        return new BigInteger(paramValue);
    }

    private static Date parseDate(String paramValue) throws ParseException {
        return new Date(paramValue);
    }
    private static LocalDate parseLocalDate(String paramValue)throws DateTimeParseException {
        return LocalDate.parse(paramValue);
    }

    private static LocalDateTime parseLocalDateTime(String paramValue)throws DateTimeParseException {
        return LocalDateTime.parse(paramValue);
    }
}
