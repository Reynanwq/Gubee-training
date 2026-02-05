package srp.violation;

import java.time.LocalDate;

public class Employee {
    private String id;
    private String name;
    private String department;
    private double salary;

    public Employee(String id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Responsabilidade 1: Gerenciar dados do funcionário
    public String getEmployeeDetails() {
        return "ID: " + id + ", Name: " + name + ", Department: " + department;
    }

    // Responsabilidade 2: Calcular salário (regra de negócio)
    public double calculateYearlySalary() {
        return salary * 12;
    }

    // Responsabilidade 3: Persistência (salvar em banco)
    public void saveToDatabase() {
        System.out.println("Saving employee " + name + " to database...");
        // Código para salvar no banco de dados
    }

    // Responsabilidade 4: Gerar relatório
    public void generateReport() {
        System.out.println("Generating report for employee: " + name);
        // Código para gerar relatório
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
