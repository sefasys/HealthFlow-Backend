package com.healthflow.infrastructure.repository;

import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.port.repository.IClinicRegistrarRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryClinicRegistrarRepository implements IClinicRegistrarRepository {
    Map<UUID, ClinicRegistrar> clinicRegistrarMap;

    public InMemoryClinicRegistrarRepository(){
        clinicRegistrarMap = new HashMap<>();
    }

    public List<ClinicRegistrar> getClinicRegistrars() {
        return List.copyOf(clinicRegistrarMap.values());
    }

    public void addClinicRegistrar(ClinicRegistrar clinicRegistrar){
        clinicRegistrarMap.put(clinicRegistrar.getStaff().getUser().getUniqueId(),clinicRegistrar);
    }

    // Repository
    public Optional<ClinicRegistrar> findByUniqueId(UUID uniqueId) {
        return Optional.ofNullable(clinicRegistrarMap.get(uniqueId));
    }

    public Optional<ClinicRegistrar> findByNationalId(NationalId nationalId){
        if (nationalId != null) {
            return clinicRegistrarMap.entrySet().stream()
                    .filter(entry -> entry.getValue()
                            .getStaff()
                            .getUser()
                            .getNationalId()
                            .equals(nationalId)).map(Map.Entry::getValue)
                    .findFirst();
        } else return Optional.empty();
    }//Mesela bu class clinician class'ına çok benzedi. Acaba burada nasıl daha doğru bir mimari ile kurabilirdik? Staff üzerinden olur muydu?

    public List<ClinicRegistrar> search(String query){
        String normalizedQuery = query.toLowerCase();
        return clinicRegistrarMap.values().stream()
                .filter(
                        clinicRegistrar ->
                                (clinicRegistrar.getStaff().getUser().getName().toLowerCase().contains(normalizedQuery)
                                        || clinicRegistrar.getStaff().getUser().getSurname().toLowerCase().contains(normalizedQuery)))
                .toList();
    }

    public void update(ClinicRegistrar clinicRegistrar){
        UUID uniqueId = clinicRegistrar.getStaff().getUser().getUniqueId();

        if(clinicRegistrarMap.containsKey(uniqueId)){
            clinicRegistrarMap.replace(uniqueId, clinicRegistrar);
        }else{
            throw new InvalidUniqueIdException("There is no user with the specific ID.");
        }
    }
}
