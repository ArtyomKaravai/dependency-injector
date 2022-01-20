package com.company.testclass.testbean;

import com.company.annotation.Inject;
import com.company.testclass.testdependecy.FirstDependency;
import com.company.testclass.testdependecy.SecondDependency;

import java.util.Objects;

public class Correct implements TestClass {

    private Long id;
    private FirstDependency firstDependency;
    private SecondDependency secondDependency;

    public Correct() {
        setId();
    }

    public Correct(FirstDependency firstDependency) {
        this.firstDependency = firstDependency;
        setId();
    }

    @Inject
    public Correct(FirstDependency firstDependency, SecondDependency secondDependency) {
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
        Correct correct = (Correct) o;
        return Objects.equals(firstDependency, correct.firstDependency) && Objects.equals(secondDependency, correct.secondDependency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstDependency, secondDependency);
    }
}
