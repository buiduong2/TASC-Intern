package _02_behavioral._01_strategy.duck_simulator.composition.algorithms;

public class FlyNoWay implements Flyable {

    @Override
    public void fly() {
        System.out.println("Do nothing");
    }
    
}
