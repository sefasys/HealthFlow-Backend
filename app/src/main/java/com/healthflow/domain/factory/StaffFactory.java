package com.healthflow.domain.factory;

import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.domain.model.user.staff.Staff;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class StaffFactory {
    public Staff createStaff(
            LocalDate hireDate,
            EmploymentStatus employmentStatus,
            User user
    ){

        return new Staff(
                UUID.randomUUID(),
                hireDate,
                employmentStatus,
                user
        );
    }

}
