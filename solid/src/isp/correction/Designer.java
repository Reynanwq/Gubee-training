package isp.correction;

public class Designer implements Human, Workable, Designable{
    @Override
    public void eat() {
        System.out.println("Designer eating salad...");
    }

    @Override
    public void sleep() {
        System.out.println("Designer sleeping...");
    }

    @Override
    public void work() {
        System.out.println("Designer working...");
    }

    @Override
    public void design() {
        System.out.println("Designer creating beautiful UI...");
    }
}
