package com.company.testclass.testdependecy;

import java.util.Objects;

public class SecondDependency {

    private boolean init;

    public SecondDependency() {
        this.init = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecondDependency that = (SecondDependency) o;
        return init == that.init;
    }

    @Override
    public int hashCode() {
        return Objects.hash(init);
    }
}
