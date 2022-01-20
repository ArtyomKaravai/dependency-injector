package com.company.testclass.testbean;

import com.company.annotation.Inject;
import com.company.testclass.testdependecy.FirstDependency;
import com.company.testclass.testdependecy.SecondDependency;

import java.util.Objects;

public class WithMultipleAnnotations implements TestClass {

    private Long id;
    private FirstDependency firstDependency;
    private SecondDependency secondDependency;

    public WithMultipleAnnotations() {
        setId();
    }

    @Inject
    public WithMultipleAnnotations(FirstDependency firstDependency) {
        this.firstDependency = firstDependency;
        setId();
    }

    @Inject
    public WithMultipleAnnotations(FirstDependency firstDependency, SecondDependency secondDependency) {
        this.firstDependency = firstDependency;
        this.secondDependency = secondDependency;
        setId();
    }

    public FirstDependency getDependency1() {
        return firstDependency;
    }

    public SecondDependency getDependency2() {
        return secondDependency;
    }

    @Override
    public Long getId() {
        return id;
    }

    private void setId() {
        this.id = Math.round(Math.random() * 10000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WithMultipleAnnotations that = (WithMultipleAnnotations) o;
        return Objects.equals(firstDependency, that.firstDependency) && Objects.equals(secondDependency, that.secondDependency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstDependency, secondDependency);
    }
}
