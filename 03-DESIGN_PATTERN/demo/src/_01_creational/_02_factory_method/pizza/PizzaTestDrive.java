package _01_creational._02_factory_method.pizza;

import _01_creational._02_factory_method.pizza.product.Pizza;
import _01_creational._02_factory_method.pizza.store.ChicagoPizzaStore;
import _01_creational._02_factory_method.pizza.store.NYPizzaStore;
import _01_creational._02_factory_method.pizza.store.PizzaStore;

public class PizzaTestDrive {
    public static void main(String[] args) {
        PizzaStore nyStore = new NYPizzaStore();
        PizzaStore chicagoStore = new ChicagoPizzaStore();

        Pizza pizza = nyStore.orderPizza("Cheese");
        System.out.println("Ethan ordered a " + pizza.getName() + "\n");

        Pizza pizza2 = chicagoStore.orderPizza("cheese");
        System.out.println("Joel ordered a " + pizza2.getName() + "\n");

        /**
         * reparing: NY Style Sauce and Cheese Pizza
         * Tossing dough...
         * Adding sauce...
         * adding Toppings...
         * Grated Reggiano Cheese?
         * Bakef or 25 minute at 350
         * Cutting the pizza into diagonal slices
         * Place pizza in official PizzaStore Box
         * Ethan ordered a NY Style Sauce and Cheese Pizza
         * 
         * ChicagoStyleCheesePizza
         * Preparing: Chicago Style Deep Dish Cheese Pizza
         * Tossing dough...
         * Adding sauce...
         * adding Toppings...
         * Shredded Mozzarella Cheese
         * Bakef or 25 minute at 350
         * Cutting the pizza into square slices
         * Place pizza in official PizzaStore Box
         * Joel ordered a Chicago Style Deep Dish Cheese Pizza
         */
    }
}
