package _02_behavioral._01_strategy.duck_simulator.inheritance;

// Tạo ra một class để có thể tai sử dụng các code quack() swim()  fly() là hành vi phổ thông của vịt
public abstract class Duck {
    public void quack() {
        System.out.println("Quack quack!");
    }

    public void swim() {
        System.out.println("Swim swim");
    }

    // CHo phép tất cả các loài vịt đều có thể bay
    public void fly() {
        System.out.println("Fly fly!");
    }

    public  abstract void display();

}