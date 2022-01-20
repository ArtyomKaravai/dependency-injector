package com.company.testclass.testdependecy;

import java.util.Objects;

public class FirstDependency {

    private boolean init;

    public FirstDependency() {
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
        FirstDependency that = (FirstDependency) o;
        return init == that.init;
    }

    @Override
    public int hashCode() {
        return Objects.hash(init);
    }
}
