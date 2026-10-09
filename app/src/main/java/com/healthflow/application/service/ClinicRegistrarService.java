package com.healthflow.application.service;

import com.healthflow.application.dto.clinicregistrar.*;
import com.healthflow.application.exception.*;
import com.healthflow.application.mapper.clinicregistrar.ClinicRegistrarMapper;
import com.healthflow.application.mapper.clinicregistrar.UpdateClinicRegistrarMapper;
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
    private final UserFactory userFactory;
    private final StaffFactory staffFactory;
    private final ClinicRegistrarMapper clinicRegistrarMapper;
    private final UpdateClinicRegistrarMapper updateClinicRegistrarMapper;

    public ClinicRegistrarService(IClinicRegistrarRepository clinicRegistrarRepository, UserFactory userFactory, StaffFactory staffFactory, ClinicRegistrarMapper clinicRegistrarMapper, UpdateClinicRegistrarMapper updateClinicRegistrarMapper) {
        this.clinicRegistrarRepository = clinicRegistrarRepository;
        this.userFactory = userFactory;
        this.staffFactory = staffFactory;
        this.clinicRegistrarMapper = clinicRegistrarMapper;
        this.updateClinicRegistrarMapper = updateClinicRegistrarMapper;
    }

    public ClinicRegistrarResponseDto createClinicRegistrar(CreateClinicRegistrarRequestDto requestDto) {
        ClinicRegistrar clinicRegistrar;
        NationalId nationalId = new NationalId(requestDto.nationalId());
        if(clinicRegistrarRepository.findByNationalId(nationalId).isPresent()){
            throw new ClinicianAlreadyExistsException("There is already a clinic registrar with the same National ID.");
        }
        User user = userFactory.createUser(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                UserRole.CLINIC_REGISTRAR);
        Staff staff = staffFactory.createStaff(
                requestDto.hireDate(),
                requestDto.employmentStatus(),
                user);
        clinicRegistrar = new ClinicRegistrar(staff);//Buradaki hatayı nasıl handle edebiliriz bu kısımla ilgilenmek gerekiyor.
        clinicRegistrarRepository.addClinicRegistrar(clinicRegistrar);
        return clinicRegistrarMapper.responseDto(clinicRegistrar);
    }//Global Exception kısmında clinician tarafını da ekle. Bir de Presentation - Application katmanı ayrımlarını nasıl yapmalıyız?

    public List<ClinicRegistrarResponseDto> getClinicRegistrars() {
        return clinicRegistrarRepository.getClinicRegistrars().stream().map(clinicRegistrarMapper::responseDto).toList();
    }

    public ClinicRegistrarResponseDto findClinicRegistrarByUniqueId(UUID uniqueId) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique ID cannot be null.");
        }
        ClinicRegistrar clinicRegistrar = clinicRegistrarRepository
                .findByUniqueId(uniqueId)
                .orElseThrow(() -> new ClinicRegistrarNotfoundException("Clinic registrar not found with unique id: " + uniqueId));

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
                        () -> new ClinicRegistrarNotfoundException("Clinic registrar not found with this national id."));

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
                        .orElseThrow(() -> new ClinicRegistrarNotfoundException("Clinic registrar could not be found."));

        if (requestDto.email() != null) {
            clinicRegistrar.getStaff().getUser().updateEmail(requestDto.email());
        }
        if (requestDto.phoneNumber() != null) {
            clinicRegistrar.getStaff().getUser().updatePhoneNumber(requestDto.phoneNumber());
        }
        clinicRegistrarRepository.update(clinicRegistrar);

        return updateClinicRegistrarMapper.responseDto(clinicRegistrar);

    }

    public List<ClinicRegistrarResponseDto> searchClinicRegistrar(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidSearchQueryException("Search query cannot be null or blank.");
        }

        return clinicRegistrarRepository.search(query).stream().map(clinicRegistrarMapper::responseDto).toList();

    }


}
