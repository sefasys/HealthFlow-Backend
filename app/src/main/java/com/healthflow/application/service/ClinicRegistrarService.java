package com.healthflow.application.service;

import com.healthflow.application.dto.clinicregistrar.*;
import com.healthflow.application.exception.*;
import com.healthflow.application.mapper.clinicregistrar.ClinicRegistrarMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.Staff;
import com.healthflow.port.repository.IClinicRegistrarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClinicRegistrarService {

    private final IClinicRegistrarRepository clinicRegistrarRepository;
    private final UserService userService;
    private final StaffFactory staffFactory;
    private final ClinicRegistrarMapper clinicRegistrarMapper;


    public ClinicRegistrarService(IClinicRegistrarRepository clinicRegistrarRepository, UserService userService, StaffFactory staffFactory, ClinicRegistrarMapper clinicRegistrarMapper) {
        this.clinicRegistrarRepository = clinicRegistrarRepository;
        this.userService = userService;
        this.staffFactory = staffFactory;
        this.clinicRegistrarMapper = clinicRegistrarMapper;

    }

    public ClinicRegistrarResponseDto createClinicRegistrar(
            CreateClinicRegistrarRequestDto requestDto
    ) {
        NationalId nationalId = new NationalId(requestDto.nationalId());

        if (clinicRegistrarRepository.findByNationalId(nationalId).isPresent()) {
            throw new ClinicRegistrarAlreadyExistsException(
                    "There is already a clinic registrar with the same national ID."
            );
        }

        User user = userService.resolveUser(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                UserRole.CLINIC_REGISTRAR
        );

        Staff staff = staffFactory.createStaff(
                requestDto.hireDate(),
                requestDto.employmentStatus(),
                user
        );

        ClinicRegistrar clinicRegistrar = new ClinicRegistrar(staff);

        user.addRole(UserRole.CLINIC_REGISTRAR);

        userService.saveUser(user);
        clinicRegistrarRepository.addClinicRegistrar(clinicRegistrar);

        return clinicRegistrarMapper.responseDto(clinicRegistrar);
    }

    public List<ClinicRegistrarResponseDto> getClinicRegistrars() {
        return clinicRegistrarRepository.getClinicRegistrars().stream().map(clinicRegistrarMapper::responseDto).toList();
    }

    public ClinicRegistrarResponseDto findClinicRegistrarByUniqueId(UUID uniqueId) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique ID cannot be null.");
        }
        ClinicRegistrar clinicRegistrar = clinicRegistrarRepository
                .findByUniqueId(uniqueId)
                .orElseThrow(() -> new ClinicRegistrarNotFoundException("Clinic registrar not found with unique id: " + uniqueId));

        return clinicRegistrarMapper.responseDto(clinicRegistrar);
    }

    public ClinicRegistrarResponseDto findClinicRegistrarByNationalId(FindClinicRegistrarByNationalIdRequestDto requestDto) {
        if (requestDto.nationalId() == null) {
            throw new InvalidNationalIdException("National ID cannot be null.");
        }
        NationalId nationalId = new NationalId(requestDto.nationalId());
        ClinicRegistrar clinicRegistrar = clinicRegistrarRepository
                .findByNationalId(nationalId)
                .orElseThrow(
                        () -> new ClinicRegistrarNotFoundException("Clinic registrar not found with this national id."));

        return clinicRegistrarMapper.responseDto(clinicRegistrar);
    }

    public UpdateClinicRegistrarResponseDto updateClinicRegistrar(UUID uniqueId, UpdateClinicRegistrarRequestDto requestDto) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique id can not be null.");
        }

        if (requestDto.email() == null && requestDto.phoneNumber() == null) {

            throw new InvalidUpdateRequestException("At least one field must be provided for update.");
        }

        ClinicRegistrar clinicRegistrar =
                clinicRegistrarRepository
                        .findByUniqueId(uniqueId)
                        .orElseThrow(() -> new ClinicRegistrarNotFoundException("Clinic registrar could not be found."));

        clinicRegistrar.getStaff().getUser().updateContactDetails(
                requestDto.email(),
                requestDto.phoneNumber()
        );

        clinicRegistrarRepository.update(clinicRegistrar);

        return clinicRegistrarMapper.updateResponseDto(clinicRegistrar);

    }

    public List<ClinicRegistrarResponseDto> searchClinicRegistrar(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidSearchQueryException("Search query cannot be null or blank.");
        }

        return clinicRegistrarRepository.search(query).stream().map(clinicRegistrarMapper::responseDto).toList();

    }


}
