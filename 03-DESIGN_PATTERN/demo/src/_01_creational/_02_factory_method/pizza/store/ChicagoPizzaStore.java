package _01_creational._02_factory_method.pizza.store;

import _01_creational._02_factory_method.pizza.product.ChicagoStyleCheesePizza;
import _01_creational._02_factory_method.pizza.product.Pizza;

/**
 * Đây là một Creator
 */
public class ChicagoPizzaStore extends PizzaStore {

    @Override
    protected Pizza createPizza(String type) {

        if (type.equals("cheese")) {
            System.out.println("ChicagoStyleCheesePizza");
        } else if (type.equals("pepperon")) {
            System.out.println("ChicagoStylePepperonPizza");
        }

        return new ChicagoStyleCheesePizza();
    }

}
