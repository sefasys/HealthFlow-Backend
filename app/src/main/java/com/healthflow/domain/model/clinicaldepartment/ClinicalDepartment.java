package com.healthflow.domain.model.clinicaldepartment;

import com.healthflow.domain.model.user.staff.Clinician;
import java.util.List;

public interface ClinicalDepartment {

  String getName();

  String getCode();

  String getDescription();

  boolean isActive();

  List<Clinician> getClinicians();
}
