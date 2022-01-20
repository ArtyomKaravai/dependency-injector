package com.company;

import com.company.di.InjectorImpl;
import com.company.di.Provider;
import com.company.test.Animal;
import com.company.test.Ear;
import com.company.test.Heart;
import com.company.test.Pig;
import com.company.test.Test;
import javafx.util.Pair;

import java.lang.reflect.InvocationTargetException;


public class Main {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        InjectorImpl injector = new InjectorImpl();

        injector.bind(Heart.class, Heart.class);

        injector.bind(Ear.class, Ear.class);

        injector.bindSingleton(Animal.class, Pig.class);

        Animal animal1 = injector.getProvider(Animal.class).getInstance();

        Animal animal2 = injector.getProvider(Animal.class).getInstance();

        System.out.println(animal1);
        System.out.println(animal2);
    }
}
