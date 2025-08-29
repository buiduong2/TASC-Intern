package com.core._04_oop;

/*
 * MỘt class trìu tượng có các thuộc tính chung
 */
public abstract class Bird {
    private String name;
    private String color;

    // Tạo ra một biến staticđể có thể truy cập vào mọi nơi trong chương trình
    private static int birdCount = 0;

    public Bird(String name, String color) {
        this.name = name;
        this.color = color;
        Bird.birdCount++;
    }

    public static void printTotalBirdCount() {
        System.out.println("total: " + birdCount);
    }

    // Tùy vào loài chim mà chúng có thể kêu khác nhau
    // Abstract method cần triển khai lại
    abstract void makeSound();

    // Tất cả các loài chim đều có thể đi bộ bằng 2 chân
    public void walk() {
        System.out.println("walk by two legs");
    }

    public void swim() {
        System.out.println("Swim 1 times");
    }

    public void swim(int times) {
        System.out.println("Swim " + times + " times");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
