package mg.itu.prom16.build;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.annotation.Configuration;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.mapping.MappingHandler;
import mg.itu.prom16.mapping.Rest;
import mg.itu.prom16.mapping.VerbMapping;
import mg.itu.prom16.util.AnnotationUtils;

public class PackageScanner {

    public static Class<?>[] CLASSES = null;

    public static Class<?>[] getCLASSES(String... packageNames) throws ClassNotFoundException, IOException {
        if (CLASSES == null) {
            CLASSES = new Class[0];
            if (packageNames == null || packageNames.length == 0 || Arrays.stream(packageNames).allMatch(pk -> pk == null || pk.isEmpty())) {
                CLASSES = findClasses("");
            } else {
                for (String packageName : packageNames) {
                    if (packageName != null && !packageName.isEmpty()) {
                        CLASSES = mergeArrays(CLASSES, findClasses(packageName));
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
            if (AnnotationUtils.hasAnnotation(clazz, annotationClass)) {
                if (packageNames.length == 0 || Arrays.stream(packageNames).anyMatch(packageName -> clazz.getPackage().getName().startsWith(packageName))) {
                    annotatedClasses.add(clazz);
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
        Map<VerbMapping, Mapping> map = new HashMap<>();
        try {
            Class<?>[] classes = getCLASSES(annotationClass, packageName);

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
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                scanDirectory(new File(resource.getFile()), packageName, classSet);
            } else if (resource.getProtocol().equals("jar")) {
                scanJar(resource, packageName, classSet);
            }
        }

        return classSet.toArray(new Class<?>[0]);
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

    private static void scanJar(URL resource, String packageName, Set<Class<?>> classSet) throws IOException, ClassNotFoundException {
        JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = jarConnection.getJarFile()) {
            if (jarFile.getEntry("framework_2624_scan.marker") != null) {
                scanJarFile(jarFile, packageName, classSet);
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
            if (AnnotationUtils.hasAnnotation(clazz, Component.class) || AnnotationUtils.hasAnnotation(clazz, Configuration.class)) {
                classSet.add(clazz);
            }
        } catch (Throwable ignored) {
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
                    Mapping mapping = new Mapping(clazz, method, url.getUrl());
                    methods.put(url, mapping);

                    if (mapping.getMethod().isAnnotationPresent(Rest.class)) {
                        mapping.setRest(true);
                    }
                }
            }
        } catch (SecurityException e) {
            throw new BuildException("Failed to access methods in class: " + clazz.getName(), e);
        }
    }

    private static Class<?>[] mergeArrays(Class<?>[] first, Class<?>[] second) {
        Class<?>[] result = new Class[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
