package dip.correction;

public class Television implements Switchable{
    @Override
    public void turnOn() {
        System.out.println("TV: Television turned on");
    }

    @Override
    public void turnOff() {
        System.out.println("TV: Television turned off");
    }
}
