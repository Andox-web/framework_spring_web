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

public class VerbMapping {
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
            if (url.equals(verbMapping.url)) {
                for (String entry : verbMapping.ListVerb) {
                    if (ListVerb.contains(entry)) {
                        return true;
                    }
                }
            }    
        }
        return false;
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

    
}
