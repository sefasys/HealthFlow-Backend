package com.healthflow.infrastructure.persistence.repository;

import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.entity.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataStaffRepository extends JpaRepository<StaffEntity, UUID> {


    @Query("""
            select s from StaffEntity s
            join fetch s.user
            where s.staffType = :type
        """)
    List<StaffEntity> findAllByStaffType(StaffType type); //burada n+1'den ötürü mü bunu ekledik?

    Optional<StaffEntity> findByUserIdAndStaffType(UUID userId, StaffType type);

    @Query("""
           select s from StaffEntity s
           join fetch s.user u
           where u.nationalId = :nationalId and s.staffType = :type
           """)
    Optional<StaffEntity> findByNationalIdAndStaffType(String nationalId, StaffType type);

    @Query("""
           select s from StaffEntity s
           join fetch s.user u
           where s.staffType = :type
             and (lower(u.name) like lower(concat('%', :q, '%'))
              or lower(u.surname) like lower(concat('%', :q, '%')))
           """)
    List<StaffEntity> search(@Param("q") String query, @Param("type") StaffType type);

}
