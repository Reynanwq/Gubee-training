package isp.violation;

public class Programmer implements Worker{
    @Override
    public void work() {
        System.out.println("Programming...");
    }

    @Override
    public void eat() {
        System.out.println("Eating at desk...");
    }

    @Override
    public void sleep() {
        System.out.println("Sleeping...");
    }

    @Override
    public void code() {
        System.out.println("Writing code...");
    }

    // Violação: Programador não faz design
    @Override
    public void design() {
        throw new UnsupportedOperationException("Programmer doesn't design!");
    }

    // Violação: Programador não faz testes (em algumas empresas)
    @Override
    public void test() {
        System.out.println("Testing code...");
    }
}
