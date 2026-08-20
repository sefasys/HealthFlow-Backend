package clinicaldepartment;

import user.staff.Clinician;

import java.util.List;

public interface ClinicalDepartment {

    String getName();
    String getCode();
    String getDescription();
    boolean isActive();
    List<Clinician> getClinicians();

}
