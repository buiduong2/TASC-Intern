package _02_behavioral._01_strategy.duck_simulator.composition.algorithms;

public class FlyWithWings implements Flyable {

    @Override
    public void fly() {
        System.out.println("Fly with wings");
    }

}
