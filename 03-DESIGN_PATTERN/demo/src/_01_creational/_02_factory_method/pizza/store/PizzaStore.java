package _01_creational._02_factory_method.pizza.store;

import _01_creational._02_factory_method.pizza.product.Pizza;

/**
 * Bussiness Logic
 */
public abstract class PizzaStore {

    public Pizza orderPizza(String type) {
        Pizza pizza = createPizza(type);

        // Code cũ
        // Pizza pizza;
        /**
         * - Class thuộc logioc nghiệp vụ lại quan tâm đến việc khởi tạo - khác với
         * logic nghiệp vụ
         * if (type.equals(“cheese”)) {
         * pizza = new CheesePizza();
         * } else if (type.equals(“greek”)) {
         * pizza = new GreekPizza();
         * } else if (type.equals(“pepperoni”)) {
         * pizza = new PepperoniPizza();
         * }
         * 
         * // Sau này thay đổi Code - luôn thay đổi theo thơi gian
         * 
         * }else if (type.equals(“clam”) {
         * pizza = new ClamPizza();
         * } else if (type.equals(“veggie”) {
         * pizza = new VeggiePizza();
         * }
         */

        // Phần này sẽ luôn luôn như thế
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();

        return pizza;
    }

    protected abstract Pizza createPizza(String type);
}
