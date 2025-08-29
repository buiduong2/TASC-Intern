package com.core._04_oop;

public class Duck extends Bird {

    public Duck(String name, String color) {
        super(name, color);
    }

    @Override
    public void walk() {
        System.out.println("Walk like Duck");
    }

    @Override
    void makeSound() {
        System.out.println("Make Sound: Quack quack");
    }

}
