package com.company.di;

import java.lang.reflect.InvocationTargetException;

public interface Provider<T> {

    T getInstance();
}
