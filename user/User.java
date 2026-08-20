package user;

import java.time.LocalDate;
import java.util.List;

public class User{

    private Long uniqueID;
    private NationalId nationalId;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private List<UserRole> userRoleList;

}