package srp.correction;

public class EmployeeRepository {
    public void save(Employee employee) {
        System.out.println("Saving employee " + employee.getName() + " to database...");
        // Código de persistência
    }

    public Employee findById(String id) {
        // Código para buscar do banco
        return null;
    }
}
