package com.company.di.impl;

import com.company.container.Injector;
import com.company.container.impl.InjectorImpl;
import com.company.exception.BindingNotFoundException;
import com.company.exception.ConstructorNotFoundException;
import com.company.exception.TooManyConstructorsException;
import com.company.testclass.testbean.Correct;
import com.company.testclass.testbean.TestClass;
import com.company.testclass.testbean.WithMultipleAnnotations;
import com.company.testclass.testbean.WithoutAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.company.testclass.testbean.WithoutAnnotationsAndEmptyConstructor;
import com.company.testclass.testdependecy.FirstDependency;
import com.company.testclass.testdependecy.SecondDependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;


class InjectorImplTest {

    private Injector injector;

    @BeforeEach
    private void init() {
        injector = new InjectorImpl();
    }

    @Test
    void bindWithoutAnnotation() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        injector.bind(TestClass.class, WithoutAnnotations.class);
        assertEquals(new WithoutAnnotations(), injector.getProvider(TestClass.class).getInstance());

    }

    @Test
    void bindWithoutAnnotationsAndEmptyConstructor() {
        assertThrows(ConstructorNotFoundException.class, () -> injector.bind(TestClass.class, WithoutAnnotationsAndEmptyConstructor.class));
    }

    @Test
    void bindWithMultipleAnnotations() {
        assertThrows(TooManyConstructorsException.class, () -> injector.bind(TestClass.class, WithMultipleAnnotations.class));
    }

    @Test
    void testCorrectDependencyInjection() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TestClass testClass = new Correct(new FirstDependency(), new SecondDependency());
        injector.bind(FirstDependency.class, FirstDependency.class);
        injector.bind(SecondDependency.class, SecondDependency.class);
        injector.bind(TestClass.class, Correct.class);
        assertEquals(testClass, injector.getProvider(TestClass.class).getInstance());
    }

    @Test
    void testWithMissingBeanToInject() {
        injector.bind(FirstDependency.class, FirstDependency.class);
        injector.bind(TestClass.class, Correct.class);
        assertThrows(BindingNotFoundException.class, () -> injector.getProvider(TestClass.class));
    }

    @Test
    void testWithMissingBeanInContainer() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertNull(injector.getProvider(TestClass.class).getInstance());
    }

    @Test
    void testSingletonBind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        injector.bindSingleton(TestClass.class, Correct.class);
        injector.bind(FirstDependency.class, FirstDependency.class);
        injector.bind(SecondDependency.class, SecondDependency.class);
        TestClass testClass1 = injector.getProvider(TestClass.class).getInstance();
        TestClass testClass2 = injector.getProvider(TestClass.class).getInstance();
        assertEquals(testClass1.getId(), testClass2.getId());
    }

    @Test
    void testPrototypeBind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        injector.bind(TestClass.class, Correct.class);
        injector.bind(FirstDependency.class, FirstDependency.class);
        injector.bind(SecondDependency.class, SecondDependency.class);
        TestClass testClass1 = injector.getProvider(TestClass.class).getInstance();
        TestClass testClass2 = injector.getProvider(TestClass.class).getInstance();
        assertNotEquals(testClass1.getId(), testClass2.getId());
    }
}
