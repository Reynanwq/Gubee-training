package ocp.correction;

public class AreaCalculator {
    public double calculateTotalArea(Shape... shapes) {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.calculateArea();
        }
        return total;
    }
}
