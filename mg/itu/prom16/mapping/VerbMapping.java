package mg.itu.prom16.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.util.PathComparator;

public class VerbMapping{
    private static final Map<Class<? extends Annotation>, String> annotationMap = new HashMap<>();

    static {
        annotationMap.put(GetMapping.class, "GET");
        annotationMap.put(PostMapping.class, "POST");
        annotationMap.put(PutMapping.class, "PUT");
        annotationMap.put(DeleteMapping.class, "DELETE");
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
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VerbMapping)) {
            return false;
        }
        VerbMapping verbMapping =(VerbMapping) obj;

        // Comparaison basée sur l'URL
        boolean urlComparison = PathComparator.matches(this.url,verbMapping.url);
        
        if (!urlComparison) {
            // Si les URLs sont différentes, on renvoie la comparaison de ces URLs
            return false;
        }

        // Si les URLs sont égales, on compare les éléments dans ListVerb
        // Utilisation de la méthode contains pour voir si la ListVerb contient au moins un élément en commun
        return verbMapping.ListVerb.stream()
            .anyMatch(this.ListVerb::contains);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    public static VerbMapping getVerbMapping(Method method,Class<? extends Annotation> URL){
        Set<String> set = new HashSet<>();
        for (Class<? extends Annotation> Annotation : annotationMap.keySet()) {
            if (method.isAnnotationPresent(Annotation)) {
                set.add(annotationMap.get(Annotation));
            }
        }
        if (set.isEmpty()) {
            return null;
        }

        VerbMapping verbMapping = new VerbMapping();
        String urlString = (method.getDeclaringClass().isAnnotationPresent(URL) ? (Url.class.cast(method.getDeclaringClass().getAnnotation(URL))).value() : "") + 
                           (method.isAnnotationPresent(URL) ? (Url.class.cast(method.getAnnotation(URL))).value() : "");
        verbMapping.setUrl(urlString);
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
}
