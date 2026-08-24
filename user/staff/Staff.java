package user.staff;

import user.User;

import java.time.LocalDate;

public class Staff {
    private String employeeId;
    private LocalDate hireDate;
    private EmploymentStatus employmentStatus;
    private User user;

    public Staff(String employeeId, LocalDate hireDate, EmploymentStatus employmentStatus, User user) {
        this.employeeId = employeeId;
        this.hireDate = hireDate;
        this.employmentStatus = employmentStatus;
        this.user = user;
    }
}
