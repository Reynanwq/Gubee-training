package ocp.correction;

public record Rectangle(double width, double height) implements Shape {
    @Override
    public double calculateArea() {
        return width * height;
    }
}

