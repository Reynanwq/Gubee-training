package lsp.correction;

public class Sparrow implements FlyingBird{
    @Override
    public void eat() {
        System.out.println("Sparrow eating...");
    }

    @Override
    public void fly() {
        System.out.println("Sparrow flying high!");
    }
}
