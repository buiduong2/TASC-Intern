package _02_behavioral._01_strategy.duck_simulator.inheritance;

public class RubberDuck extends Duck {

    @Override
    public void display() {
        System.out.println("I look like a Rubber Duck");
    }

    // Rubber Duck là đồ chơi và nó không quack() cunxg không fly()

    // Vậy là có một vết nhơ trong thiết kế của chúng ta
    // Tại sao họ ko nghĩ fly() và quack() là tính năng. Nó khá là vui

    // Ta tạm ghi đè nó
    // nhưng tương lai ko ai biết sẽ có những loại vit nào được sinh ra. Ta sẽ phải
    // làm gì
    @Override
    public void fly() {

        System.out.println("Do nothing");
    }

    @Override
    public void quack() {
        System.out.println("do nothing");
    }

}

// Code bị lặp lại xuyên xuyết các subClass khi cần ghi đè hành vi
// Runtime ko thể thay đổi hành vi được (VD họ muốn ăn quả gì đó lại cho bay)
// Khó để khi chúng ta tạo ra một class mới thì lại về tìm hiểu xem Duck có
// những hành vi gì
// Thay đổi class Duck có thể khiến cho tạo ra ảnh hưởng ko mong muốn ở Class
// Duck