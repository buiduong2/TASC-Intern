```java
 Pizza orderPizza() {
    Pizza pizza = new Pizza();
    pizza.prepare();
    pizza.bake();
    pizza.cut();
    pizza.box();
    return pizza;
 }

// Chúng ta cần thêm nhiều kiểu pizza hơn
  Pizza orderPizza(String type) {
    Pizza pizza;
    if (type.equals(“cheese”)) {
        pizza = new CheesePizza();
    } else if (type.equals(“greek”)) {
        pizza = new GreekPizza();
    } else if (type.equals(“pepperoni”)) {
        pizza = new PepperoniPizza();
    }
    pizza.prepare();
    pizza.bake();
    pizza.cut();
    pizza.box();
    return pizza;
 }

 // Nhưng gặp phải áp lực khi mà thêm nhiều kiểu Pizza hơn nữa:
 // Nếu mà chúng ta thay đổi các kiểu Pizza. Các Pizza cũ có thể ko còn được sử dụng nữa thì sao
 // Không đóng mở
  Pizza orderPizza(String type) {
    Pizza pizza;
    if (type.equals(“cheese”)) {
        pizza = new CheesePizza();
    } else if (type.equals(“greek”)) {
        pizza = new GreekPizza();
    } else if (type.equals(“pepperoni”))  {
        pizza = new PepperoniPizza();
    } else if (type.equals(“clam”)) {
        pizza = new ClamPizza();
    } else if (type.equals(“veggie”)) {
        pizza = new VeggiePizza();
    }

    // Các phần này sẽ phải luôn luôn giống nhau
    pizza.prepare();
    pizza.bake();
    pizza.cut();
    pizza.box();
    return pizza;
  }
```

- Bây giờ chúng ta cần đóng gói việc tạo Object

- Chúng ta sẽ sửa lại PizzaStore
    - Cho nó một field có trách nhiệm khởi tạo (được truyền thông qua constructor)