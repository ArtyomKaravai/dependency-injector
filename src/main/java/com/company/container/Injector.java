package com.company.container;

import java.lang.reflect.InvocationTargetException;

public interface Injector {

    <T> Provider<T> getProvider(Class<T> type)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    <T> void bind(Class<T> intf, Class<? extends T>impl);

    <T> void bindSingleton(Class<T> intf, Class<? extends T>impl);
}
