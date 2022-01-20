package com.company.test;

import com.company.di.Inject;

public class Pig implements Animal {

    private Heart heart;
    private Ear ear;
    private long pigId;

    @Inject
    public Pig(Heart heart, Ear ear) {
        this.ear = ear;
        this.heart = heart;
        pigId = Math.round(Math.random() * 10000);
    }

    @Override
    public String toString() {
        return "Pig{" +
                "heart=" + heart +
                ", ear=" + ear +
                ", pigId=" + pigId +
                '}';
    }
}
