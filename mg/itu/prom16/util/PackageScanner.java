package mg.itu.prom16.util;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.lang.annotation.Annotation;

public class PackageScanner {
    public static  List<Class<?>> getClassesFromPackage(String packageName,Class<? extends Annotation> annotationClass) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(packagePath);
    
        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.getPath());
                for (File file : directory.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> class1=Class.forName(className);
                        if (class1.isAnnotationPresent(annotationClass)) {
                            classes.add(class1);  
                        }
                    }
                }
            }
        }
    
        return classes;
    }
}
