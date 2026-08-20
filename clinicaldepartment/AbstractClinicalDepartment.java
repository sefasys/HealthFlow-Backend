package clinicaldepartment;

import user.staff.Clinician;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClinicalDepartment implements ClinicalDepartment {

    private String departmentName;
    private String departmentCode;
    private String description;
    private boolean activity;
    private List<Clinician> clinicians = new ArrayList<>();

    protected AbstractClinicalDepartment(String departmentName, String departmentCode, String description, boolean activity) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.description = description;
        this.activity = activity;
    }

    @Override
    public String getName() {
        return departmentName;
    }

    @Override
    public String getCode() {
        return departmentCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isActive() {
        return activity;
    }

    @Override // bu kısma iyi bak.
    public List<Clinician> getClinicians() {
        return clinicians;
    }
}
