package com.company.test;

public class Ear {

    private long earId;

    public Ear() {
        earId = Math.round(Math.random() * 10000);
    }

    public Ear(Long id) {
        earId = Math.round(Math.random() * 10000);
    }

    @Override
    public String toString() {
        return "Ear{" +
                "earId=" + earId +
                '}';
    }
}
