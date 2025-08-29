package _01_creational._02_factory_method.pizza.product;

/**
 * Concrete Product
 */
public class ChicagoStyleCheesePizza extends Pizza {
    public ChicagoStyleCheesePizza() {
        name = "Chicago Style Deep Dish Cheese Pizza";
        dough = "Extra Thick Crust Dough";
        sauce = "Plum Tomato Sauce";

        toppings.add("Shredded Mozzarella Cheese");
    }

    // Kiểu Chigo cũng ghi đè cut() vì nó có kiểu cắt khác vào trong hình vuông
    @Override
    public void cut() {
        System.out.println("Cutting the pizza into square slices");
    }
}
