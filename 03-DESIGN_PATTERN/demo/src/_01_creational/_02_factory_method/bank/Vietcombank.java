package _01_creational._02_factory_method.bank;

// Một SubChild có thể được return từ factory method
public class Vietcombank implements Bank {

    @Override
    public String getBankName() {
        return "VietcomBank";
    }

}
