package _01_creational._02_factory_method.pizza.product;

import java.util.ArrayList;
import java.util.List;

/**
 * Product
 */
public abstract class Pizza {
    protected String name;
    protected String dough;
    protected String sauce;

    protected List<String> toppings = new ArrayList<>();

    public void prepare() {
        System.out.println("Preparing: " + name);
        System.out.println("Tossing dough...");
        System.out.println("Adding sauce...");
        System.out.println("adding Toppings...");
        for (String topping : toppings) {
            System.out.println(" \t " + topping);
        }
    }

    public void bake() {
        System.out.println("Bakef or 25 minute at 350");
    }

    public void cut() {
        System.out.println("Cutting the pizza into diagonal slices");
    }

    public void box() {
        System.out.println("Place pizza in official PizzaStore Box");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDough() {
        return dough;
    }

    public void setDough(String dough) {
        this.dough = dough;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

}
