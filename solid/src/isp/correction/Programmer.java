package isp.correction;

public class Programmer implements Human, Workable, Programmable, Testable {
    @Override
    public void eat() {
        System.out.println("Programmer eating pizza...");
    }

    @Override
    public void sleep() {
        System.out.println("Programmer sleeping at desk...");
    }

    @Override
    public void work() {
        System.out.println("Programmer working...");
    }

    @Override
    public void code() {
        System.out.println("Programmer writing clean code...");
    }

    @Override
    public void test() {
        System.out.println("Programmer writing unit tests...");
    }
}
