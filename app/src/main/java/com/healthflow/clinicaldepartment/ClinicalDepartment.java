package com.healthflow.clinicaldepartment;

import com.healthflow.user.staff.Clinician;
import java.util.List;

public interface ClinicalDepartment {

  String getName();

  String getCode();

  String getDescription();

  boolean isActive();

  List<Clinician> getClinicians();
}
