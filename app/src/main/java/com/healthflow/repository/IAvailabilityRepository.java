package com.healthflow.repository;


import com.healthflow.entity.availability.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAvailabilityRepository extends JpaRepository<Availability, UUID> {


}
