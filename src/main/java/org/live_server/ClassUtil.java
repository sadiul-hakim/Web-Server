package org.live_server;

import org.live_server.annotations.GET;
import org.live_server.enumeration.HttpMethod;
import org.live_server.system_object.ServerRequest;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassUtil {
    private static final String NOT_FOUND_MESSAGE = "{\"message\":\"404 Not Fount\"}";
    private static final Logger LOGGER = Logger.getLogger(ClassUtil.class.getName());

    public static Object handleRequest(ServerRequest request, List<Class<?>> controllerClass) {

        HttpMethod httpMethod = Objects.requireNonNull(HttpMethod.of(request.getMethod()));

        switch (httpMethod) {
            case GET -> {
                Map<Class<?>, Map<Method, GET>> getRequests = new HashMap<>();
                for (Class<?> controller : controllerClass) {
                    Map<Method, GET> annotatedMethod = getAnnotatedMethod(controller, GET.class);
                    getRequests.put(controller, annotatedMethod);
                }

                for (Map.Entry<Class<?>, Map<Method, GET>> classMapEntry : getRequests.entrySet()) {

                    for (Map.Entry<Method, GET> entry : classMapEntry.getValue().entrySet()) {
                        if (entry.getValue().path().equals(request.getUrl())) {
                            return callTheRequestedMethod(entry, classMapEntry.getKey(), request);
                        }
                    }
                }
            }
            case POST -> {
            }
            default -> {
                return NOT_FOUND_MESSAGE;
            }
        }

        return NOT_FOUND_MESSAGE;
    }

    private static Object callTheRequestedMethod(Map.Entry<Method, GET> entry, Class<?> parentClass, ServerRequest request) {
        Method method = entry.getKey();
        assert method != null;

        try {
            Object instance = parentClass.getDeclaredConstructor().newInstance();
            return method.invoke(instance, request);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
                        getListOfClassUnderAPackage(packageName, "", directory, classes);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }

        return classes;
    }

    private static void getListOfClassUnderAPackage(String packageName, String subPackage, File directory, List<Class<?>> classes) {
        try {
            for (java.io.File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    String[] split = file.getAbsolutePath().split("\\\\");
                    getListOfClassUnderAPackage(packageName, split[split.length - 1], file, classes);
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
            LOGGER.log(Level.INFO, e.getMessage());
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

    public static List<Class<?>> getAnnotatedClass(Class<? extends Annotation> annotation) {
        List<Class<?>> classesInCurrentPackage = getClassesInCurrentPackage();
        List<Class<?>> annotatedClassList = new ArrayList<>();

        for (Class<?> cls : classesInCurrentPackage) {
            if (cls.isAnnotationPresent(annotation)) {
                annotatedClassList.addLast(cls);
            }
        }

        return annotatedClassList;
    }
}
