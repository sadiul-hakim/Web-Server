package org.live_server;

import org.live_server.annotations.Controller;
import org.live_server.annotations.GET;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ClassUtil {

    public static Object handleRequest(String requestPath) {
        List<Class<?>> classesInCurrentPackage = getClassesInCurrentPackage();
        List<Class<?>> annotatedClass = getAnnotatedClass(classesInCurrentPackage, Controller.class);

        Map<Class<?>, Map<Method, GET>> getRequests = new HashMap<>();
        for (Class<?> cls : annotatedClass) {
            Map<Method, GET> annotatedMethod = getAnnotatedMethod(cls, GET.class);
            getRequests.put(cls, annotatedMethod);
        }

        for (Map.Entry<Class<?>, Map<Method, GET>> classMapEntry : getRequests.entrySet()) {

            for (Map.Entry<Method, GET> entry : classMapEntry.getValue().entrySet()) {
                if (entry.getValue().path().equals(requestPath)) {
                    Method method = entry.getKey();

                    try {
                        Object instance = classMapEntry.getKey().getDeclaredConstructor().newInstance();
                        assert method != null;
                        return method.invoke(instance);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return "";
    }

    private static List<Class<?>> getClassesInCurrentPackage() {

        // Get the current package name
        String packageName = ClassUtil.class.getPackage().getName();

        // Get all classes in the current package
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            for (URL resource : Collections.list(classLoader.getResources(path))) {
                if (resource.getFile().contains("target")) {

                    // Load classes only from compiled sources, not from libraries or JARs
                    String filePath = resource.getFile();
                    java.io.File directory = new java.io.File(filePath);
                    if (directory.isDirectory()) {
                        getClassList(packageName, "", directory, classes);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return classes;
    }

    private static void getClassList(String packageName, String subPackage, File directory, List<Class<?>> classes) {
        try {
            for (java.io.File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    String[] split = file.getAbsolutePath().split("\\\\");
                    getClassList(packageName, split[split.length - 1], file, classes);
                } else if (file.getName().endsWith(".class")) {
                    String className;
                    if (subPackage.isEmpty()) {
                        className = STR."\{packageName}.\{file.getName().substring(0, file.getName().length() - 6)}";
                    } else {
                        className = STR."\{packageName}\{'.'}\{subPackage}.\{file.getName().substring(0, file.getName().length() - 6)}";
                    }

                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<Method, GET> getAnnotatedMethod(Class<?> annotatedClass, Class<? extends Annotation> annotation) {
        Map<Method, GET> methodList = new HashMap<>();

        Method[] methods = annotatedClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                GET annotationCls = (GET) method.getAnnotation(annotation);
                methodList.put(method, annotationCls);
            }
        }

        return methodList;
    }

    private static List<Class<?>> getAnnotatedClass(List<Class<?>> classes, Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClassList = new ArrayList<>();

        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(annotation)) {
                annotatedClassList.addLast(cls);
            }
        }

        return annotatedClassList;
    }
}
