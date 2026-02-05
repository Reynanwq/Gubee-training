import ocp.correction.*;
import srp.correction.*;
import dip.correction.*;
import isp.correction.*;
import lsp.correction.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DOS PRINCÍPIOS SOLID ===\n");

        demoSRP();
        demoOCP();
        demoLSP();
        demoISP();
        demoDIP();
    }

    private static void demoSRP() {
        System.out.println("1. SRP - Single Responsibility Principle:");
        Employee emp = new Employee("001", "John Doe", "Engineering", 5000);
        SalaryCalculator calc = new SalaryCalculator();
        EmployeeRepository repo = new EmployeeRepository();
        ReportGenerator report = new ReportGenerator();

        System.out.println(emp.getEmployeeDetails());
        System.out.println("Yearly salary: $" + calc.calculateYearlySalary(emp));
        repo.save(emp);
        report.generateEmployeeReport(emp);
        System.out.println();
    }

    private static void demoOCP() {
        System.out.println("2. OCP - Open/Closed Principle:");
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        Shape triangle = new Triangle(3, 4);

        AreaCalculator calculator = new AreaCalculator();
        double totalArea = calculator.calculateTotalArea(circle, rectangle, triangle);

        System.out.println("Circle area: " + circle.calculateArea());
        System.out.println("Rectangle area: " + rectangle.calculateArea());
        System.out.println("Triangle area: " + triangle.calculateArea());
        System.out.println("Total area: " + totalArea);
        System.out.println();
    }

    private static void demoLSP() {
        System.out.println("3. LSP - Liskov Substitution Principle:");

        FlyingBird sparrow = new Sparrow();
        SwimmingBird duck = new Duck();
        Bird ostrich = new Ostrich();

        sparrow.eat();
        sparrow.fly();

        duck.eat();
        duck.swim();

        ostrich.eat();

        System.out.println();
    }

    private static void demoISP() {
        System.out.println("4. ISP - Interface Segregation Principle:");

        Programmer programmer = new Programmer();
        Designer designer = new Designer();

        System.out.println("Programmer:");
        programmer.eat();
        programmer.work();
        programmer.code();

        System.out.println("\nDesigner:");
        designer.eat();
        designer.work();
        designer.design();

        System.out.println();
    }

    private static void demoDIP() {
        System.out.println("5. DIP - Dependency Inversion Principle:");

        Switchable light = new LightBulb();
        Switchable fan = new Fan();
        Switchable tv = new Television();

        Switch lightSwitch = new Switch(light);
        Switch fanSwitch = new Switch(fan);
        Switch tvSwitch = new Switch(tv);

        System.out.println("Operating light:");
        lightSwitch.operate();

        System.out.println("\nOperating fan:");
        fanSwitch.operate();

        System.out.println("\nOperating TV:");
        tvSwitch.operate();
    }
}