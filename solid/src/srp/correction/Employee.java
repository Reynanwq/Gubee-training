package srp.correction;

public class Employee {
    private final String id;
    private final String name;
    private final String department;
    private final double salary;

    public Employee(String id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getEmployeeDetails() {
        return "ID: " + id + ", Name: " + name + ", Department: " + department;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }
}


