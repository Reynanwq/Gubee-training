package ocp.correction;

public sealed interface Shape permits Circle, Rectangle, Triangle {
    double calculateArea();
}
