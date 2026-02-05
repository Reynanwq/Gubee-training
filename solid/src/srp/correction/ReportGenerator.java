package srp.correction;

public class ReportGenerator {
    public void generateEmployeeReport(Employee employee) {
        System.out.println("""
            === Employee Report ===
            Name: %s
            Department: %s
            ID: %s
            ======================
            """.formatted(employee.getName(), employee.getDepartment(), employee.getId()));
    }
}
