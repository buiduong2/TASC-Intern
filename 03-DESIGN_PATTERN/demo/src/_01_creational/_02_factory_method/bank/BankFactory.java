package _01_creational._02_factory_method.bank;

public class BankFactory {
    private BankFactory() {

    }

    public static final Bank getBank(BankType bankType) {
        switch (bankType) {
            case TPBANK:
                return new TPBank();
            case VIETCOMBANK:
                return new Vietcombank();
            default:
                throw new IllegalArgumentException("This bank Type is unsupported");
        }
    }
}
