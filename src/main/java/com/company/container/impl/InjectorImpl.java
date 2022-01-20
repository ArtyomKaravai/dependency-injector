package com.company.container.impl;

import com.company.enm.BeanScope;
import com.company.annotation.Inject;
import com.company.container.Injector;
import com.company.container.Provider;
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

public class InjectorImpl implements Injector {

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
        validateClass(impl);
        container.put(intf, new Pair<>(BeanScope.PROTOTYPE, impl));
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        validateClass(impl);
        container.put(intf, new Pair<>(BeanScope.SINGLETON, impl));
    }


    /**
     * Counts the number of constructors with the required annotation.
     * If the number of annotations is greater than 1, then TooManyConstructorsException() is thrown.
     * If the number of constructors is less than 1, then a constructor with no arguments is searched,
      otherwise a ConstructorNotFoundException() is thrown.
     * @return true.
     */
    private boolean validateClass(Class<?> impl) {
        long numberOfConstructors = Arrays.stream(impl.getConstructors())
                .filter(x -> x.isAnnotationPresent(ANNOTATION_CLASS))
                .count();
        if(numberOfConstructors > 1) {
            throw new TooManyConstructorsException();
        }
        if(numberOfConstructors < 1) {
            Arrays.stream(impl.getConstructors())
                    .filter(x -> x.getParameterCount() == 0)
                    .findFirst()
                    .orElseThrow(ConstructorNotFoundException::new);
        }
        return true;
    }

    /**
     * Looking for a pair in a container.
     * If there is no pair, then null is returned.
     * If there is a pair, then it returns the bean.
     * @return Bean or null.
     */
    private <T> Object findBean(Class<T> type)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Pair<BeanScope, Object> scopeAndBean = container.get(type);
        if(scopeAndBean != null && scopeAndBean.getKey().equals(BeanScope.PROTOTYPE)) {                 //PROTOTYPE
            return (T) getPrototypeBean(scopeAndBean);
        }
        if(scopeAndBean != null && scopeAndBean.getKey().equals(BeanScope.SINGLETON)) {                 //SINGLETON
            return (T) getSingletonBean(type, scopeAndBean);
        }
        return null;
    }

    /**
     * @return Prototype bean.
     */
    private <T> Object getPrototypeBean(Pair<BeanScope, Object> scopeAndBean)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return buildBean((Class<?>) scopeAndBean.getValue());
    }

    /**
     * Checks if the bean is initialized.
     * If yes, then returns it.
     * If not, then it is initialized.
     * @return Singleton bean.
     */
    private <T> Object getSingletonBean(Class<T> type, Pair<BeanScope, Object> scopeAndBean)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(scopeAndBean.getValue() instanceof Class<?>) {                                                                //lazy initialization
            Object initializedBean = buildBean((Class<?>) scopeAndBean.getValue());
            Pair<BeanScope, Object> scopeAndInitializedSingletonBean = new Pair<>(BeanScope.SINGLETON, initializedBean);
            container.put(type, scopeAndInitializedSingletonBean);
            return (T) initializedBean;
        }
        return (T) scopeAndBean.getValue();
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
     * Looks for a constructor with an annotation,
      if not found, looks for a constructor with no arguments.
     * @return A constructor with an annotation or a constructor without arguments and annotations.
     */
    private Constructor<?> findCorrectConstructor(Class<?> impl) {
        return Arrays.stream(impl.getConstructors())
                .filter(x -> x.isAnnotationPresent(ANNOTATION_CLASS))
                .findFirst()
                .orElse(Arrays.stream(impl.getConstructors())
                        .filter(x -> x.getParameterCount() == 0)
                        .findFirst()
                        .orElseThrow(ConstructorNotFoundException::new));
    }

    /**
     * Looks for the dependencies needed to create the bean in the container.
     * If there are no required dependencies, then a BindingNotFoundException() is thrown.
     * @return An array of beans or an empty array.
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
