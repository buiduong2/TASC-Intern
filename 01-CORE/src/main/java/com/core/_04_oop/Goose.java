package com.core._04_oop;

// Sub Class của Bird
public class Goose extends Bird implements Flyable {

    public Goose(String name, String color) {
        super(name, color);
    }

    // Ngỗng có thể bay nên nó sẽ có một method giúp cho bay được
    @Override
    public void fly() {
        System.out.println("Fly !!!");
    }

    // Ngoonxg
    @Override
    public void makeSound() {
        System.out.println("Make sound: Honk Honk");
    }

}
