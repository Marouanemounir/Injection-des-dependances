package ma.enset.bdcc.framework.annotations;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationContext {
    private Map<Class<?>, Object> instances = new HashMap<>();
    private List<Class<?>> componentClasses;

    public AnnotationContext(String basePackage) throws Exception {
        componentClasses = findClasses(basePackage);

        List<Class<?>> targetClasses = new ArrayList<>();
        for (Class<?> aClass : componentClasses) {
            if (aClass.isAnnotationPresent(MyComponent.class)) {
                targetClasses.add(aClass);
            }
        }

        for (Class<?> aClass : targetClasses) {
            if (!instances.containsKey(aClass)) {
                createInstance(aClass);
            }
        }

        // Setter & Field Injection
        for (Object instance : instances.values()) {
            Class<?> aClass = instance.getClass();

            // Field Injection
            for (Field field : aClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    field.setAccessible(true);
                    Object dependency = getBeanOrInstantiate(field.getType());
                    field.set(instance, dependency);
                }
            }

            // Setter Injection
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MyAutowired.class) && method.getName().startsWith("set")) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        Object dependency = getBeanOrInstantiate(parameterTypes[0]);
                        method.invoke(instance, dependency);
                    }
                }
            }
        }
    }

    private Object createInstance(Class<?> aClass) throws Exception {
        if (instances.containsKey(aClass))
            return instances.get(aClass);

        for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(MyAutowired.class)) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    params[i] = getBeanOrInstantiate(parameterTypes[i]);
                }
                Object instance = constructor.newInstance(params);
                instances.put(aClass, instance);
                registerInterfaces(aClass, instance);
                return instance;
            }
        }

        Object instance = aClass.getDeclaredConstructor().newInstance();
        instances.put(aClass, instance);
        registerInterfaces(aClass, instance);
        return instance;
    }

    private void registerInterfaces(Class<?> aClass, Object instance) {
        for (Class<?> iface : aClass.getInterfaces()) {
            instances.put(iface, instance);
        }
    }

    private Object getBeanOrInstantiate(Class<?> target) throws Exception {
        if (instances.containsKey(target)) {
            return instances.get(target);
        }
        for (Class<?> aClass : componentClasses) {
            if (aClass.isAnnotationPresent(MyComponent.class)) {
                if (target.isAssignableFrom(aClass)) {
                    return createInstance(aClass);
                }
            }
        }
        throw new RuntimeException("Bean non trouvé pour " + target.getName());
    }

    public Object getBean(Class<?> target) {
        return instances.get(target);
    }

    private List<Class<?>> findClasses(String scannedPackage) throws Exception {
        String path = scannedPackage.replace('.', '/');
        URL resource = getClass().getClassLoader().getResource(path);
        File directory = new File(resource.toURI());
        List<Class<?>> classes = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            for (String file : directory.list()) {
                if (file.endsWith(".class")) {
                    String className = scannedPackage + '.' + file.substring(0, file.length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }
}
