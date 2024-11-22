package mg.itu.prom16.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.mapping.MappingHandler;
import mg.itu.prom16.mapping.VerbMapping;
import mg.itu.prom16.annotation.mapping.GetMapping;
import mg.itu.prom16.annotation.mapping.Rest;
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.build.DuplicateUrlException;

public class PackageScanner {

    public static Map<VerbMapping, Mapping> getMapping(String packageName, Class<? extends Annotation> annotationClass) throws BuildException {
        Map<VerbMapping, Mapping> map = new TreeMap<>();
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
                                        getMethods(class1, map);
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

    private static void getMethods(Class<?> clazz, Map<VerbMapping, Mapping> methods) throws BuildException {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                VerbMapping url =MappingHandler.getUrl(method);
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
