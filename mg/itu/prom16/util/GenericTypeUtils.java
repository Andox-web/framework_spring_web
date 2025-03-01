package mg.itu.prom16.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypeUtils {

    // Fonction générique pour récupérer les types génériques
    public static Type[] getGenericTypes(Class<?> clazz) {
        Type[] genericTypes = null;

        // Vérifier si la classe implémente une interface générique
        if (clazz.getGenericInterfaces().length > 0) {
            for (Type type : clazz.getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    genericTypes = parameterizedType.getActualTypeArguments();
                }
            }
        }

        // Vérifier si la classe est une sous-classe d'une classe générique
        if (genericTypes == null && clazz.getGenericSuperclass() != null) {
            Type type = clazz.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                genericTypes = parameterizedType.getActualTypeArguments();
            }
        }
        
        // Retourner les types génériques (null si aucun n'est trouvé)
        return genericTypes;
    }

}
