package _02_behavioral._01_strategy.duck_simulator.composition.algorithms;

import _02_behavioral._01_strategy.duck_simulator.composition.model.Duck;

public class DecoyDuck extends Duck {

    @Override
    public void display() {
        System.out.println("I am a DecoyDuck");
    }

}
