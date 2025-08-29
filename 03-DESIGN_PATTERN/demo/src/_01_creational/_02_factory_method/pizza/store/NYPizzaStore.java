package _01_creational._02_factory_method.pizza.store;

import _01_creational._02_factory_method.pizza.product.NyStyleCheesPizza;
import _01_creational._02_factory_method.pizza.product.Pizza;

/**
 * Đây là một Creator
 */
public class NYPizzaStore extends PizzaStore {

    @Override
    protected Pizza createPizza(String type) {

        if (type.equals("cheese")) {
            System.out.println("NyStyleCheesePizza");
        } else if (type.equals("pepperon")) {
            System.out.println("NyStylePepperonPizza");
        }

        return new NyStyleCheesPizza();
    }

}
