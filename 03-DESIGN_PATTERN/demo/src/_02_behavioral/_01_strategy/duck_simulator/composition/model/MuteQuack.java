package _02_behavioral._01_strategy.duck_simulator.composition.model;

import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.Quackable;

public class MuteQuack implements Quackable {

    @Override
    public void quack() {
        System.out.println("Do nothing");
    }

}
