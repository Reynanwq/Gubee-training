package ocp.violation;

public class AreaCalculator {
    public double calculateArea(Object shape) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            return Math.PI * circle.getRadius() * circle.getRadius();
        } else if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            return rectangle.getWidth() * rectangle.getHeight();
        }
        // Para adicionar um triângulo, precisamos MODIFICAR esta classe
        throw new IllegalArgumentException("Shape not supported");
    }
}
