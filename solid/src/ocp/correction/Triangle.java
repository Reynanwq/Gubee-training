package ocp.correction;

public record Triangle(double base, double height) implements Shape {
    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }
}