package com.core._04_oop;

public class App {
    public static void main(String[] args) {
        Duck duck = new Duck("Duck", "black");

        Goose goose = new Goose("Goose", "White");
        Flyable gooseFly = goose;

        // Abstract inherit method
        goose.walk(); // walk by two legs

        // Override walk method
        duck.walk(); // Walk like Duck - Override
        goose.walk(); // walk by two legs - Inherit

        // Interface vs abstract
        gooseFly.fly();
        // Vẫn là Ngỗng nhưng ta có thể đem đi khắp nơi và ta biết được rằng nó có
        // method fly
        // duck.fly(); // bản thân ko phải loài chim nào cũng bay được. Nên ta chỉ cho
        // phép cá subclass implmenet flyable

        // OverLoading
        duck.swim(); // Swim 1 times
        duck.swim(10); // Swim 1 times

        // Các method có cùng một mong đợi khi chạy chương trình nhưng logic khác nhau.
        // ta có thể đặt cùng tên để tránh nghĩ ra nhiều tên phức tạp
    }
}
