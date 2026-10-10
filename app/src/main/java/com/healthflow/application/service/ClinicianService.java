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
    private final UserService userService;
    private final StaffFactory staffFactory;
    private final ClinicianMapper clinicianMapper;

    public ClinicianService(IClinicianRepository clinicianRepository, UserService userService, ClinicianMapper clinicianMapper, StaffFactory staffFactory){
        this.clinicianRepository = clinicianRepository;
        this.userService = userService;
        this.clinicianMapper = clinicianMapper;
        this.staffFactory = staffFactory;
    }


    public ClinicianResponseDto createClinician(
            CreateClinicianRequestDto requestDto
    ) {
        NationalId nationalId = new NationalId(requestDto.nationalId());

        if (clinicianRepository.findByNationalId(nationalId).isPresent()) {
            throw new ClinicianAlreadyExistsException(
                    "There is already a clinician with the same national ID."
            );
        }

        User user = userService.resolveUser(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                UserRole.CLINICIAN
        );

        Staff staff = staffFactory.createStaff(
                requestDto.hireDate(),
                requestDto.employmentStatus(),
                user
        );

        Clinician clinician = new Clinician(staff);

        user.addRole(UserRole.CLINICIAN);

        userService.saveUser(user);
        clinicianRepository.addClinician(clinician);

        return clinicianMapper.responseDto(clinician);
    }



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

        if (requestDto == null) {
            throw new InvalidUpdateRequestException(
                    "Update request cannot be null."
            );
        }

        if (requestDto.email() == null && requestDto.phoneNumber() == null) {

            throw new InvalidUpdateRequestException("At least one field must be provided for update.");
        }

        Clinician clinician =
                clinicianRepository
                        .findByUniqueId(uniqueId)
                        .orElseThrow(() -> new ClinicianNotFoundException("Clinician could not be found."));

        clinician.getStaff().getUser().updateContactDetails(
                requestDto.email(),
                requestDto.phoneNumber()
        );

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
