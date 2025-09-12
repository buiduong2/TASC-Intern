package _02_behavioral._01_strategy.duck_simulator.composition.model;

import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.FlyWithWings;
import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.Quack;

public class MallarDuck extends Duck {

    public MallarDuck() {
        this.setFlyable(new FlyWithWings());
        this.setQuackable(new Quack());
    }

    @Override
    public void display() {
        System.out.println("I am a MallarDuck");
    }

}
