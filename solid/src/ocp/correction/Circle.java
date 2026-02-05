package ocp.correction;

public record Circle(double radius) implements Shape{
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
