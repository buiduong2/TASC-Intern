package _02_behavioral._01_strategy.duck_simulator.composition;

import _02_behavioral._01_strategy.duck_simulator.composition.algorithms.FlyWithWings;
import _02_behavioral._01_strategy.duck_simulator.composition.model.Duck;
import _02_behavioral._01_strategy.duck_simulator.composition.model.MallarDuck;
import _02_behavioral._01_strategy.duck_simulator.composition.model.RubberDuck;

public class Main {
    public static void main(String[] args) {
        Duck mallard = new MallarDuck();
        mallard.performQuack();
        mallard.performFly();
        Duck rubber = new RubberDuck();
        rubber.performFly();    

        // TỰ nhiên có khả năng lắp tên lửa vào đít và nó có thể bay
        // Thay đổi hành vi tại runtime
        rubber.setFlyable(new FlyWithWings());
        rubber.performFly();
    }
}
