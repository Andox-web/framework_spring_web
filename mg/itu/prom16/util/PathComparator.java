package mg.itu.prom16.util;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathComparator {

    /**
     * Vérifie si l'input correspond au template avec des {paramètres}
     * @param template Modèle avec des {paramètres}
     * @param input Chaîne à tester
     * @return true si la structure correspond
     */
    public static boolean matches(String path1, String path2) {
        return path2.matches(buildRegex(path1))||path1.matches(buildRegex(path2));
    }

    /**
     * Extrait les paramètres de l'input selon le template
     * @param template Modèle avec des {paramètres}
     * @param input Chaîne contenant les valeurs
     * @return Map clé/valeur des paramètres
     */
    public static HashMap<String, String> extract(String template, String input) {
        HashMap<String, String> params = new HashMap<>();
        Matcher matcher = Pattern.compile(buildRegex(template)).matcher(input);
        
        if (matcher.find()) {
            List<String> keys = getKeys(template);
            for (int i = 0; i < keys.size(); i++) {
                params.put(keys.get(i), matcher.group(i + 1));
            }
        }
        return params;
    }

    /**
     * Construit la regex dynamiquement en 3 étapes :
     * 1. Découpe le template en parties fixes/variables
     * 2. Échappe les parties fixes
     * 3. Remplace les {param} par (.*?)
     */
    private static String buildRegex(String template) {
        StringBuilder regex = new StringBuilder("^");
        Matcher m = Pattern.compile("(\\{[^}]+\\})|([^{]+)").matcher(template);
        
        while (m.find()) {
            String part = m.group();
            if (part.startsWith("{")) {
                regex.append("(.*?)"); // Groupe capturant pour le paramètre
            } else {
                regex.append(Pattern.quote(part)); // Échappement des littéraux
            }
        }
        return regex.append("$").toString();
    }

    /**
     * Extrait les noms des paramètres du template
     */
    private static List<String> getKeys(String template) {
        List<String> keys = new ArrayList<>();
        Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(template);
        while (m.find()) keys.add(m.group(1));
        return keys;
    }
}