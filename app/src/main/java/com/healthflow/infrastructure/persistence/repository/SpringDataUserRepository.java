package com.healthflow.infrastructure.persistence.repository;

import com.healthflow.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByNationalId(String nationalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u where u.uniqueId in :ids order by u.uniqueId") //bu anotasyonlarla ne yapılıyor aslında?
    List<UserEntity> lockAllByIds(@Param("ids") Collection<UUID> ids);


}
