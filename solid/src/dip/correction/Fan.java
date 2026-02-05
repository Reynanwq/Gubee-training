package dip.correction;

public class Fan implements Switchable{
    @Override
    public void turnOn() {
        System.out.println("Fan: Fan started spinning");
    }

    @Override
    public void turnOff() {
        System.out.println("Fan: Fan stopped");
    }
}
