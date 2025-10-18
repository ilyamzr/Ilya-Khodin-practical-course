package org.example;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME)
@interface Component {
}

@Retention(RetentionPolicy.RUNTIME)
@interface Autowired{
}

interface InitializingBean
{
    void afterPropertiesSet();
}

public class MiniApplicationContext {
    private final Map<Class<?>, Object> beans = new HashMap<>();
    private final String packageToScan;

    MiniApplicationContext(String packageToScan) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.packageToScan = packageToScan;
        initialiseApplicationContext();
    }

    public void initialiseApplicationContext() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Class<?>> componentClasses = scanPackages();
        createBeans(componentClasses);
        injectingDependencies();
        initializingBeansInjection();
    }

    public void createBeans(List<Class<?>> classes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> componentClass : classes)
        {
            Object instance = componentClass.getDeclaredConstructor().newInstance();
            beans.put(componentClass, instance);
        }
    }

    public List<Class<?>> scanPackages() throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageToScan.replace('.', '/');
        URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Package not found: " + packageToScan);
        }
        File directory = new File(resource.getFile());
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Invalid package directory: " + packageToScan);
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = packageToScan + "." + file.getName().replace(".class", "");
                Class<?> componentClass = Class.forName(className);
                if (componentClass.isAnnotationPresent(Component.class)) {
                    classes.add(componentClass);
                }
            }
        }
        return classes;
    }

    public <T> T getBean(Class<T> type){
        return (T) beans.get(type);
    }

    public void injectingDependencies() throws IllegalAccessException {
        for (Object bean : beans.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    Object dependency = beans.get(fieldType);
                    if (dependency == null) {
                        throw new RuntimeException("Bean" + fieldType.getName() + "Not found");
                    }
                    field.set(bean, dependency);
                }
            }
        }
    }

    public void initializingBeansInjection()
    {
        for (Object bean : beans.values()) {
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
        }
    }

}