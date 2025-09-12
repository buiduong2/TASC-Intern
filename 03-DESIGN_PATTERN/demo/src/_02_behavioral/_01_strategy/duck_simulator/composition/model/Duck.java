package _02_behavioral._01_strategy.duck_simulator.composition.model;

import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.Flyable;
import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.Quackable;

public abstract class Duck {

    private Flyable flyable;

    private Quackable quackable;

    public void swim() {
        System.out.println("Swim swim!");
    }

    public abstract void display();

    public void performQuack() {
        quackable.quack();
    }

    public void performFly() {
        flyable.fly();
    }

    public void setFlyable(Flyable flyable) {
        this.flyable = flyable;
    }

    public void setQuackable(Quackable quackable) {
        this.quackable = quackable;
    }
}
