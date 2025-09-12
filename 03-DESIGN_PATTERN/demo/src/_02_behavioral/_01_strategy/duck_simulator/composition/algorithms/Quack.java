package _02_behavioral._01_strategy.duck_simulator.composition.algorithms;

public class Quack implements Quackable {

    @Override
    public void quack() {
        System.out.println("Quack quack!");
    }

}
