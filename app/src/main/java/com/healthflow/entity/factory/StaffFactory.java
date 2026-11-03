package com.healthflow.entity.factory;

import com.healthflow.entity.user.User;
import com.healthflow.entity.user.staff.EmploymentStatus;
import com.healthflow.entity.user.staff.Staff;
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
