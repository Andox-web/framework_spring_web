package mg.itu.prom16.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.annotation.mapping.GetMapping;
import mg.itu.prom16.annotation.mapping.PostMapping;
import mg.itu.prom16.annotation.mapping.Url;

public class VerbMapping implements Comparable {
    private static final Map<Class<? extends Annotation>, String> annotationMap = new HashMap<>();

    static {
        annotationMap.put(GetMapping.class, "GET");
        annotationMap.put(PostMapping.class, "POST");
    }

    Set<String> ListVerb;
    String url;
    
    private VerbMapping(){}
    public VerbMapping(HttpServletRequest request){
        url = request.getServletPath().trim();
        ListVerb=new HashSet<>();
        ListVerb.add(request.getMethod());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VerbMapping) {
            VerbMapping verbMapping=(VerbMapping) obj;
            System.out.println(verbMapping.url+"  "+url);
            if (url.equals(verbMapping.url)) {
                System.out.println("lien");
                for (String entry : verbMapping.ListVerb) {
                    if (this.ListVerb.contains(entry)) {
                        return true;
                    }
                }
            }    
        }
        return false;
    }
    @Override
    public int hashCode() {
        int result = url.hashCode();
        for (String verb : ListVerb) {
            result = 31 * result + verb.hashCode();
        }
        return result;
    }
    public static VerbMapping getVerbMapping(Method method,Class<? extends Annotation> URL){
        VerbMapping verbMapping = new VerbMapping();
        verbMapping.setUrl(Url.class.cast(method.getAnnotation(URL)).value());
        Set<String> set = new HashSet<>();
        for (Class<? extends Annotation> Annotation : annotationMap.keySet()) {
            if (method.isAnnotationPresent(Annotation)) {
                set.add(annotationMap.get(Annotation));
            }
        }
        verbMapping.setListVerb(set);
        return verbMapping;
    }

    public static Map<Class<? extends Annotation>, String> getAnnotationmap() {
        return annotationMap;
    }
    public Set<String> getListVerb() {
        return ListVerb;
    }
    public void setListVerb(Set<String> listVerb) {
        ListVerb = listVerb;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public String toString() {
        return "url "+url+" ListVerb "+ListVerb;
    }
    @Override
    public int compareTo(Object o) {
        if (o instanceof VerbMapping) {
            VerbMapping verbMapping = (VerbMapping) o;

            // Comparaison basée sur l'URL
            int urlComparison = this.url.compareTo(verbMapping.url);
            
            if (urlComparison != 0) {
                // Si les URLs sont différentes, on renvoie la comparaison de ces URLs
                return urlComparison;
            }

            // Si les URLs sont égales, on compare les éléments dans ListVerb
            // Utilisation de la méthode contains pour voir si la ListVerb contient au moins un élément en commun
            boolean hasCommonVerb = false;
            for (String entry : verbMapping.ListVerb) {
                if (this.ListVerb.contains(entry)) {
                    hasCommonVerb = true;
                    break;
                }
            }

            // Si un verb commun est trouvé, les objets sont considérés égaux (retour 0)
            if (hasCommonVerb) {
                return 0; // égaux
            } else {
                // Si pas de verb commun, on peut décider de renvoyer une valeur négative ou positive
                // Par exemple, on pourrait décider que la comparaison des verbes affecte l'ordre
                return 1; // Par exemple, si aucun verb commun, on les considère comme "plus grands"
            }
        }
        
        // Si l'objet n'est pas du même type, on renvoie un nombre positif (ou négatif)
        return 1; // Ou éventuellement 0 ou une valeur négative selon votre logique
    }

}
