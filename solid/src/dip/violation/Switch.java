package dip.violation;

public class Switch {
    private LightBulb lightBulb;

    public Switch() {
        this.lightBulb = new LightBulb(); // Acoplamento forte
    }

    public void operate() {
        // Só funciona com LightBulb, não com Fan
        lightBulb.turnOn();
        // Não podemos facilmente trocar para Fan
    }
}
