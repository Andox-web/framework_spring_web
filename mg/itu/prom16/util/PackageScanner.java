package mg.itu.prom16.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.mapping.MappingHandler;
import mg.itu.prom16.mapping.VerbMapping;
import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.annotation.Configuration;
import mg.itu.prom16.annotation.mapping.Rest;
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.build.DuplicateUrlException;

public class PackageScanner {

    public static Class<?>[] CLASSES = null;
    
    public static Class<?>[] getCLASSES(String... packageNames) throws ClassNotFoundException, IOException {
        if(CLASSES == null){
            CLASSES = new Class[0];
            if (packageNames == null || packageNames.length == 0 || Arrays.stream(packageNames).allMatch(pk -> pk==null || pk.isEmpty())) {
                CLASSES = findClasses("");
            } else {
                for (String string : packageNames) {
                    if (string != null && !string.isEmpty()) {
                        Class<?>[] foundClasses = findClasses(string);
                        Class<?>[] newClasses = new Class[CLASSES.length + foundClasses.length];
                        System.arraycopy(CLASSES, 0, newClasses, 0, CLASSES.length);
                        System.arraycopy(foundClasses, 0, newClasses, CLASSES.length, foundClasses.length);
                        CLASSES = newClasses;
                    }
                }
            }
        }
        return CLASSES;
    }

    public static Class<?>[] getCLASSES(Class<? extends Annotation> annotationClass, String... packageNames) throws ClassNotFoundException, IOException {
        Class<?>[] classes = getCLASSES(packageNames);
        List<Class<?>> annotatedClasses = new ArrayList<>();
        
        for (Class<?> clazz : classes) {
            if (AnnotationUtils.hasAnnotation(clazz,annotationClass)) {
                if (packageNames.length == 0) {
                    annotatedClasses.add(clazz);
                } else {
                    for (String packageName : packageNames) {
                        if (clazz.getPackage().getName().startsWith(packageName)) {
                            annotatedClasses.add(clazz);
                            break;
                        }
                    }
                }
            }
        }
        
        return annotatedClasses.toArray(new Class[0]);
    }

    public final static void scan(String... packageNames) throws BuildException, ClassNotFoundException {
        try {
            getCLASSES(packageNames);
            System.out.println("Classes scanned: " + CLASSES.length);
        } catch (IOException e) {
            throw new BuildException("Failed to scan packages: " + String.join(", ", packageNames), e);
        }
    }

    public static Map<VerbMapping, Mapping> getMapping(String packageName, Class<? extends Annotation> annotationClass) throws BuildException, ClassNotFoundException {
        Map<VerbMapping, Mapping> map = new TreeMap<>();
        try {
            Class<?>[] classes = getCLASSES(annotationClass,packageName);
            
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(annotationClass)) {
                    getMethods(clazz, map);
                }
            }
        } catch (IOException e) {
            throw new BuildException("Failed to scan package: " + packageName, e);
        }
        if (map.isEmpty()) {
            throw new BuildException("No controller is found in package " + packageName);
        }
        return map;
    }

    private static Class<?>[] findClasses(String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classSet = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // Ajouter le nom du package dans le format attendu pour la recherche
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                scanDirectory(file, packageName, classSet);
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                try (JarFile jarFile = jarConnection.getJarFile()) {
                    scanJarFile(jarFile, packageName, classSet);
                }
            }
        }

        return classSet.toArray(new Class<?>[0]);  // Convertir le Set en tableau
    }

    private static void scanDirectory(File directory, String packageName, Set<Class<?>> classSet) throws ClassNotFoundException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + file.getName() + ".", classSet);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + file.getName().replace(".class", "");
                checkAndAddSpringBean(className, classSet);
            }
        }
    }

    private static void scanJarFile(JarFile jarFile, String packageName, Set<Class<?>> classSet) throws ClassNotFoundException {
        String packagePath = packageName.replace('.', '/');

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.endsWith(".class") && entryName.startsWith(packagePath)) {
                String className = entryName.replace("/", ".").replace(".class", "");
                checkAndAddSpringBean(className, classSet);
            }
        }
    }

    private static void checkAndAddSpringBean(String className, Set<Class<?>> classSet) {
        try {
            Class<?> clazz = Class.forName(className);
            if (AnnotationUtils.hasAnnotation(clazz,Component.class) || 
                AnnotationUtils.hasAnnotation(clazz,Configuration.class)) {
                classSet.add(clazz);
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();   
        }
    }

    private static void getMethods(Class<?> clazz, Map<VerbMapping, Mapping> methods) throws BuildException {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                VerbMapping url = MappingHandler.getUrl(method);
                if (url != null) {
                    
                    if (methods.containsKey(url)) {
                        throw new DuplicateUrlException(url.getUrl());
                    }
                    Mapping mapping = new Mapping(clazz, method);
                    methods.put(url, mapping);

                    // Vérifier si la méthode est annotée avec @Rest
                    if (mapping.getMethod().isAnnotationPresent(Rest.class)) {
                        mapping.setRest(true);   
                    }
                }
            }
        } catch (SecurityException e) {
            throw new BuildException("Failed to access methods in class: " + clazz.getName(), e);
        }
    }
}
