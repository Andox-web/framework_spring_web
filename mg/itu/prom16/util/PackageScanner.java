package mg.itu.prom16.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.annotation.Get;
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.build.DuplicateUrlException;

public class PackageScanner {

    public static Map<String, Mapping> getMapping(String packageName, Class<? extends Annotation> annotationClass) throws BuildException {
        Map<String, Mapping> map = new HashMap<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String packagePath = packageName.replace(".", "/");
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.getPath());
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile() && file.getName().endsWith(".class")) {
                                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                                try {
                                    Class<?> class1 = Class.forName(className);
                                    if (class1.isAnnotationPresent(annotationClass)) {
                                        Map<String, Mapping> methods = new HashMap<>();
                                        getMethods(class1, methods);
                                        for (String url : methods.keySet()) {
                                            if (map.containsKey(url)) {
                                                throw new DuplicateUrlException(url);
                                            }
                                        }
                                        map.putAll(methods);
                                    }
                                } catch (ClassNotFoundException e) {
                                    throw new BuildException("Class not found: " + className, e);
                                }
                            }
                        }
                    } else {
                        throw new BuildException("Failed to list files in directory: " + directory.getPath());
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException("Failed to scan package: " + packageName, e);
        }
        if(map.isEmpty()){
            throw new BuildException("no controller is found in package "+packageName);
        }
        return map;
    }

    private static void getMethods(Class<?> clazz, Map<String, Mapping> methods) throws BuildException {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                Get an = method.getAnnotation(Get.class);

                if (an != null) {
                    String url = an.url();
                    if (url.isBlank()) {
                        url=method.getName();
                    }
                    String trimmedUrl = url.trim();
                    if (methods.containsKey(trimmedUrl)) {
                        throw new DuplicateUrlException(trimmedUrl);
                    }
                    methods.put(trimmedUrl, new Mapping(clazz, method));
                }
            }
        } catch (SecurityException e) {
            throw new BuildException("Failed to access methods in class: " + clazz.getName(), e);
        }
    }
}
