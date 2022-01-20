package com.company.test;

import com.company.di.Inject;

public class Heart {

    private long heartId;

    @Inject
    public Heart() {
        heartId = Math.round(Math.random() * 10000);
    }

    @Override
    public String toString() {
        return "Heart{" +
                "heartId=" + heartId +
                '}';
    }
}
