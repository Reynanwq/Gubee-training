package dip.correction;

public class Switch {
    private final Switchable device;

    // Injeção de dependência via construtor
    public Switch(Switchable device) {
        this.device = device;
    }

    public void operate() {
        device.turnOn();
        // Podemos adicionar lógica complexa aqui
        System.out.println("Device operated successfully");
    }

    public void toggle() {
        // Lógica de toggle
        System.out.println("Toggling device...");
        device.turnOff();
    }
}
