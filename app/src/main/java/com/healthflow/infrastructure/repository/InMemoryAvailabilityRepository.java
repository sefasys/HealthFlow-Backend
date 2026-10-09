package com.healthflow.infrastructure.repository;

import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.port.repository.IAvailabilityRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class InMemoryAvailabilityRepository implements IAvailabilityRepository {
    private final Map<UUID, Availability> availabilityMap;

    public InMemoryAvailabilityRepository(){
        this.availabilityMap = new HashMap<>();
    }


    @Override
    public Availability save(Availability availability) {
        return availabilityMap.put(availability.getUniqueId(), availability);
    }

    @Override
    public Optional<Availability> findById(UUID availabilityId) {
        return Optional.ofNullable(availabilityMap.get(availabilityId));
    }

    @Override
    public List<Availability> findByClinicianId(UUID clinicianId){
        return availabilityMap.values().stream().filter(availability ->
                availability.getClinician()
                        .getStaff()
                        .getUser()
                        .getUniqueId()
                        .equals(clinicianId)).toList();
    }

    @Override
    public List<Availability> findByClinicianIdAndDate(UUID clinicianId, LocalDate date) {
        return availabilityMap.values().stream().filter(availability ->
                        availability.getClinician()
                                .getStaff()
                                .getUser()
                                .getUniqueId()
                                .equals(clinicianId))
                .filter(availability ->
                        availability.getDate().equals(date))
                .toList();
    }

    @Override
    public List<Availability> findByStatus(AvailabilityStatus availabilityStatus) {
        return availabilityMap.values().stream().filter(availability -> availability.getStatus().equals(availabilityStatus)).toList();
    }
}
