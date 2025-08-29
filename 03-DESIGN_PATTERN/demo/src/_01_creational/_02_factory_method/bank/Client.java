package _01_creational._02_factory_method.bank;

/** Một nơi đẹp trai nào đó cần sử dụng đến Bank */
public class Client {
    public static void main(String[] args) {
        Bank bank = BankFactory.getBank(BankType.TPBANK);
        System.out.println(bank.getBankName()); // TPBank
    }
}
