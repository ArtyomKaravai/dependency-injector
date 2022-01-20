package com.company.di;

import java.lang.reflect.InvocationTargetException;

public interface Injector {

    <T> Provider<T> getProvider(Class<T> type);

    <T> void bind(Class<T> intf, Class<? extends T>impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    <T> void bindSingleton(Class<T> intf, Class<? extends T>impl)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
