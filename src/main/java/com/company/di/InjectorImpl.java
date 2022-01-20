package com.company.di;

import com.company.exception.BindingNotFoundException;
import com.company.exception.ConstructorNotFoundException;
import com.company.exception.TooManyConstructorsException;
import javafx.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectorImpl implements Injector{

    private final Map<Class<?>, Pair<BeanScope, Object>> container;
    private static final Class<Inject> ANNOTATION_CLASS = Inject.class;

    {
        container = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        Pair<BeanScope, Object> beanPair = container.get(type);
        return new Provider<T>() {
            @Override
            public T getInstance() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
                if(beanPair != null && beanPair.getKey().equals(BeanScope.PROTOTYPE)) {
                    return (T) buildInstance((Class<?>) beanPair.getValue());
                }
                if(beanPair != null && beanPair.getKey().equals(BeanScope.SINGLETON)) {
                    if(beanPair.getValue() instanceof Class<?>) {
                        Object initializedBean = buildInstance((Class<?>) beanPair.getValue());
                        Pair<BeanScope, Object> initializedSingletonBeanPair = new Pair<>(BeanScope.SINGLETON, initializedBean);
                        container.put(type, initializedSingletonBeanPair);
                        return (T) initializedBean;
                    }
                    return (T) beanPair.getValue();
                }
                return null;
            }
        };
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        container.put(intf, new Pair<>(BeanScope.PROTOTYPE, impl));
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        container.put(intf, new Pair<>(BeanScope.SINGLETON, impl));
    }

    private Constructor<?> findCorrectConstructor(Class<?> impl) {
        long numberOfConstructors = Arrays.stream(impl.getConstructors())
                .filter(x -> x.isAnnotationPresent(ANNOTATION_CLASS))
                .count();
        if(numberOfConstructors > 1) {
            throw new TooManyConstructorsException();
        }
        if(numberOfConstructors < 1) {
            return Arrays.stream(impl.getConstructors())
                    .filter(x -> x.getParameterCount() == 0)
                    .findFirst()
                    .orElseThrow(ConstructorNotFoundException::new);
        }
        return Arrays.stream(impl.getConstructors())
                .filter(x -> x.isAnnotationPresent(ANNOTATION_CLASS))
                .findFirst()
                .get();
    }

    private Object[] findDependencyBeans(Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(x -> {
                    Object o = null;
                    try {
                        o = getProvider(x).getInstance();
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                    if(o == null) {
                        throw new BindingNotFoundException();
                    }
                    return o;})
                .toArray();
    }

    private Object buildInstance(Class<?> impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = findCorrectConstructor(impl);
        Class<?>[] argumentTypes = constructor.getParameterTypes();
        Object[] arguments = findDependencyBeans(argumentTypes);
        return impl.getDeclaredConstructor(argumentTypes).newInstance(arguments);
    }
}
