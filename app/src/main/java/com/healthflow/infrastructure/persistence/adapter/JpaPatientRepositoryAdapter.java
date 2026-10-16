package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import com.healthflow.infrastructure.persistence.mapper.PatientPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.SpringDataPatientRepository;
import com.healthflow.infrastructure.persistence.repository.SpringDataUserRepository;
import com.healthflow.port.repository.IPatientRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JpaPatientRepositoryAdapter implements IPatientRepository {

    //metotları önceden yazdığımız repository domain tarafındaki kontratlara göre alıyoruz.
    //sonrasında bu metotların implementasyonunu ise SpringDataPatientRepository'nin extend ettiği JpaRepository tarafındaki metotlarla implement ediyoruz.

    private final SpringDataPatientRepository patientRepository;
    private final SpringDataUserRepository userRepository;
    private final PatientPersistenceMapper mapper;

    public JpaPatientRepositoryAdapter(SpringDataPatientRepository patientRepository,
                                       SpringDataUserRepository userRepository,
                                       PatientPersistenceMapper mapper){
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    public List<Patient> getPatients() {
        return patientRepository.findAll().stream().map(mapper::toDomain).toList();
    }


    @Override
    public Optional<Patient> findByUniqueId(UUID uniqueId) {
        return patientRepository.findById(uniqueId).map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByNationalId(NationalId nationalId) {
        return patientRepository.findByNationalId(nationalId.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<Patient> search(String query) {
        return patientRepository.search(query).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void addPatient(Patient patient) {
        UserEntity user = userRepository.getReferenceById(patient.getUniqueId());
        patientRepository.save(mapper.toEntity(patient, user));
    }

    @Override
    @Transactional
    public void update(Patient patient) {
        PatientEntity entity = patientRepository.findById(patient.getUniqueId())
                .orElseThrow(() -> new PatientNotFoundException("Patient could not be found."));
        entity.changeBloodType(patient.getBloodType());
    }
}
