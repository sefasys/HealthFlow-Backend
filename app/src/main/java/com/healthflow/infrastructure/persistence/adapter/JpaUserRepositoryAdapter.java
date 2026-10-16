package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.SpringDataUserRepository;
import com.healthflow.port.repository.IUserRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Primary
@Profile("jpa")
public class JpaUserRepositoryAdapter implements IUserRepository {

    private final SpringDataUserRepository repository;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(SpringDataUserRepository repository,
                                    UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public User save(User user) {                      // upsert, in-memory ile aynı davranış
        repository.findById(user.getUniqueId()).ifPresentOrElse(
                entity -> {
                    entity.changeContact(user.getEmail(), user.getPhoneNumber());
                    entity.replaceRoles(Set.copyOf(user.getUserRoleList()));
                },
                () -> repository.save(mapper.toEntity(user)));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUniqueId(UUID uniqueId) {
        return repository.findById(uniqueId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByNationalId(NationalId nationalId) {
        return repository.findByNationalId(nationalId.getValue()).map(mapper::toDomain);
    }
}
