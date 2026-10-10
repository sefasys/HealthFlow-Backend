package com.healthflow.application.service;

import com.healthflow.application.dto.clinician.*;
import com.healthflow.application.exception.ClinicianAlreadyExistsException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.mapper.clinician.ClinicianMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.Staff;
import com.healthflow.port.repository.IClinicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClinicianService {

    private final IClinicianRepository clinicianRepository;
    private final UserFactory userFactory;
    private final StaffFactory staffFactory;
    private final ClinicianMapper clinicianMapper;

    public ClinicianService(IClinicianRepository clinicianRepository, UserFactory userFactory, ClinicianMapper clinicianMapper, StaffFactory staffFactory){
        this.clinicianRepository = clinicianRepository;
        this.userFactory = userFactory;
        this.clinicianMapper = clinicianMapper;
        this.staffFactory = staffFactory;
    }


    public ClinicianResponseDto createClinician(CreateClinicianRequestDto requestDto) {
        Clinician clinician;
        NationalId nationalId = new NationalId(requestDto.nationalId());
        if(clinicianRepository.findByNationalId(nationalId).isPresent()){
            throw new ClinicianAlreadyExistsException("There is already a clinician with the same National ID.");
        }
        User user = userFactory.createUser(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                UserRole.CLINICIAN);
        Staff staff = staffFactory.createStaff(
                requestDto.hireDate(),
                requestDto.employmentStatus(),
                user);
        clinician = new Clinician(staff);
        clinicianRepository.addClinician(clinician);
        return clinicianMapper.responseDto(clinician);
    }//Global Exception kısmında clinician tarafını da ekle. Bir de Presentation - Application katmanı ayrımlarını nasıl yapmalıyız?



    public List<ClinicianResponseDto> getClinicians() {
        return clinicianRepository.getClinicians().stream().map(clinicianMapper::responseDto).toList();
    }

    public ClinicianResponseDto findClinicianByUniqueId(UUID uniqueId) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique ID cannot be null.");
        }
        Clinician clinician = clinicianRepository
                .findByUniqueId(uniqueId)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician not found with unique id: " + uniqueId));

        return clinicianMapper.responseDto(clinician);
    }

    public ClinicianResponseDto findClinicianByNationalId(FindClinicianByNationalIdRequestDto requestDto) {
        if (requestDto.nationalId() == null) {
            throw new InvalidNationalIdException("National ID cannot be null.");
        }
        NationalId nationalId = new NationalId(requestDto.nationalId());
        Clinician clinician = clinicianRepository
                .findByNationalId(nationalId)
                .orElseThrow(
                        () -> new ClinicianNotFoundException("Clinician not found with this national id."));

        return clinicianMapper.responseDto(clinician);
    }

    public UpdateClinicianResponseDto updateClinician(UUID uniqueId, UpdateClinicianRequestDto requestDto) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique id can not be null.");
        }

        if (requestDto.email() == null && requestDto.phoneNumber() == null) {

            throw new InvalidUpdateRequestException("At least one field must be provided for update.");
        }

        Clinician clinician =
                clinicianRepository
                        .findByUniqueId(uniqueId)
                        .orElseThrow(() -> new ClinicianNotFoundException("Clinician could not be found."));

        if (requestDto.email() != null) {
            clinician.getStaff().getUser().updateEmail(requestDto.email());
        }
        if (requestDto.phoneNumber() != null) {
            clinician.getStaff().getUser().updatePhoneNumber(requestDto.phoneNumber());
        }
        clinicianRepository.update(clinician);

        return clinicianMapper.updateResponseDto(clinician);


    }

    public List<ClinicianResponseDto> searchClinician(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidSearchQueryException("Search query cannot be null or blank.");
        }

        return clinicianRepository.search(query).stream().map(clinicianMapper::responseDto).toList();

    }
}
