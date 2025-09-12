package _02_behavioral._01_strategy.duck_simulator.composition.model;

import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.FlyNoWay;

public class RubberDuck extends Duck {

    public RubberDuck() {
        setFlyable(new FlyNoWay());
        setQuackable(new MuteQuack());
    }

    @Override
    public void display() {
        System.out.println("I am a RubberDuck !");
    }

}
