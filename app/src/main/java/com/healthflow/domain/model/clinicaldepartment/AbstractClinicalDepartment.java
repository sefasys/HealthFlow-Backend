package com.healthflow.domain.model.clinicaldepartment;

import com.healthflow.domain.exception.InvalidDepartmentException;
import com.healthflow.domain.model.user.staff.Clinician;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClinicalDepartment implements ClinicalDepartment {

  private final String departmentName;
  private final String departmentCode;
  private final String description;
  private final boolean activity;
  private final List<Clinician> clinicians = new ArrayList<>();

  protected AbstractClinicalDepartment(
      String departmentName, String departmentCode, String description, boolean activity) {
    if(departmentName == null | departmentName.isBlank()){
      throw new InvalidDepartmentException("Department name can not be null.");
    }
    if(departmentCode == null | departmentCode.isBlank()){
      throw new InvalidDepartmentException("Department code can not be null.");
    }
    if(description == null | description.isBlank()){
      throw new InvalidDepartmentException("Department description can not be null.");
    }


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
