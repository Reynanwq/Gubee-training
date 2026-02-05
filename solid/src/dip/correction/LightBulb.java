package dip.correction;

public class LightBulb implements Switchable {
    @Override
    public void turnOn() {
        System.out.println("LightBulb: Light turned on");
    }

    @Override
    public void turnOff() {
        System.out.println("LightBulb: Light turned off");
    }
}
