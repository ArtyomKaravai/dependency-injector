package com.company.di;

import com.company.exception.BindingNotFoundException;
import com.company.exception.ConstructorNotFoundException;
import com.company.exception.TooManyConstructorsException;
import javafx.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author : Karavay Artyom
 */

public class InjectorImpl implements Injector{

    private static final Class<Inject> ANNOTATION_CLASS = Inject.class;
    private final Map<Class<?>, Pair<BeanScope, Object>> container;

    {
        container = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Optional<Object> optionalBean = Optional.ofNullable(findBean(type));
        return new Provider<T>() {
            @Override
            public T getInstance() {
                return (T) optionalBean.orElse(null);
            }
        };
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        container.put(intf, new Pair<>(BeanScope.PROTOTYPE, impl));
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        container.put(intf, new Pair<>(BeanScope.SINGLETON, impl));
    }


    /**
     * Looks for a bean in a container.
     * If the component is a Prototype, then a new instance is returned.
     * If Singleton, then the bean from the container is returned.
     * If it hasn't been initialized yet, then it is initialized, placed in a container, and returned.
     * @return Bean or null.
     */
    private <T> Object findBean(Class<T> type)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Pair<BeanScope, Object> scopeAndBean = container.get(type);
        if(scopeAndBean != null && scopeAndBean.getKey().equals(BeanScope.PROTOTYPE)) {                 //PROTOTYPE
            return (T) buildBean((Class<?>) scopeAndBean.getValue());
        }
        if(scopeAndBean != null && scopeAndBean.getKey().equals(BeanScope.SINGLETON)) {                 //SINGLETON
            if(scopeAndBean.getValue() instanceof Class<?>) {                            //lazy initialization
                Object initializedBean = buildBean((Class<?>) scopeAndBean.getValue());
                Pair<BeanScope, Object> scopeAndInitializedSingletonBean = new Pair<>(BeanScope.SINGLETON, initializedBean);
                container.put(type, scopeAndInitializedSingletonBean);
                return (T) initializedBean;
            }
            return (T) scopeAndBean.getValue();
        }
        return null;
    }

    /**
     * Injects the dependencies and creates the required bean.
     * @return Bean.
     */
    private Object buildBean(Class<?> impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = findCorrectConstructor(impl);
        Class<?>[] argumentTypes = constructor.getParameterTypes();
        Object[] arguments = findDependencyBeans(argumentTypes);
        return impl.getDeclaredConstructor(argumentTypes).newInstance(arguments);
    }


    /**
     * Counts the number of constructors with the required annotation.
     * If the number of annotations is greater than 1, then TooManyConstructorsException() is thrown.
     * If the number of constructors is less than 1, then a constructor with no arguments is searched,
     * otherwise a ConstructorNotFoundException() is thrown.
     * If there is only one annotated constructor, it is returned.
     * @return A constructor with an annotation or a constructor without arguments and annotations.
     */
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


    /**
     * Looks for the dependencies needed to create the bean in the container.
     * If there are no required dependencies, then a BindingNotFoundException() is thrown.
     * @return An array of bins or an empty array.
     */
    private Object[] findDependencyBeans(Class<?>[] classes)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object[] dependencyBeans = new Object[classes.length];
        for(int i = 0; i < classes.length; i++) {
            Object bean = findBean(classes[i]);
            if (bean == null) {
                throw new BindingNotFoundException();
            }
            dependencyBeans[i] = bean;
        }
        return dependencyBeans;
    }
}
