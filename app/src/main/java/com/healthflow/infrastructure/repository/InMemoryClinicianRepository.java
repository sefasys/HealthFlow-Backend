package com.healthflow.infrastructure.repository;

import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.IClinicianRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryClinicianRepository implements IClinicianRepository {

    Map<UUID, Clinician> clinicianMap;

    public InMemoryClinicianRepository(){
        clinicianMap = new HashMap<>();
    }

    public List<Clinician> getClinicians() {
        return List.copyOf(clinicianMap.values());
    }

    public void addClinician(Clinician clinician){
        clinicianMap.put(clinician.getStaff().getUser().getUniqueId(),clinician);
    }

    // Repository
    public Optional<Clinician> findByUniqueId(UUID uniqueId) {
        return Optional.ofNullable(clinicianMap.get(uniqueId));
    }

    public Optional<Clinician> findByNationalId(NationalId nationalId){
        if (nationalId != null) {
            return clinicianMap.entrySet().stream()
                    .filter(entry -> entry.getValue()
                                                        .getStaff()
                                                        .getUser()
                                                        .getNationalId()
                                                        .equals(nationalId)).map(Map.Entry::getValue)
                    .findFirst();
        } else return Optional.empty();
    }

    public List<Clinician> search(String query){
        String normalizedQuery = query.toLowerCase();
        return clinicianMap.values().stream()
                .filter(
                        clinician ->
                                (clinician.getStaff().getUser().getName().toLowerCase().contains(normalizedQuery)
                                        || clinician.getStaff().getUser().getSurname().toLowerCase().contains(normalizedQuery)))
                .toList();
    }

    public void update(Clinician clinician){
        UUID uniqueId = clinician.getStaff().getUser().getUniqueId();

        if(clinicianMap.containsKey(uniqueId)){
            clinicianMap.replace(uniqueId, clinician);
        }else{
            throw new InvalidUniqueIdException("There is no user with the specific ID.");
        }
    }

}
