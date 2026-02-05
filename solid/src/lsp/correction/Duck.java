package lsp.correction;

public class Duck implements FlyingBird, SwimmingBird{
    @Override
    public void eat() {
        System.out.println("Duck eating...");
    }

    @Override
    public void fly() {
        System.out.println("Duck flying...");
    }

    @Override
    public void swim() {
        System.out.println("Duck swimming...");
    }
}
