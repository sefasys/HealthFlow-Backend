package com.healthflow.repository.patient;

import com.healthflow.entity.user.Gender;
import com.healthflow.entity.patient.BloodType;
import com.healthflow.entity.patient.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PatientRepositoryAdapter implements IPatientRepository {

    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "user.name", "u.name",
            "user.surname", "u.surname",
            "user.birthDate", "u.birthDate");

    //hata gibi gözüküyor ama hata yokmuş. adapter kullanmayı hiç beğenmedim.
    private static final String SEARCH_CONDITIONS = """
            where (lower(u.name) like lower(concat('%', :query, '%'))
                   or lower(u.surname) like lower(concat('%', :query, '%'))
                   or u.nationalId like concat(:query, '%'))
              and (:gender is null or u.gender = :gender)
              and (:bloodType is null or p.bloodType = :bloodType)
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            entityManager.persist(patient);
            return patient;
        }
        return entityManager.merge(patient);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Patient.class, id));
    }

    @Override
    public Optional<Patient> findDetailedById(UUID id) {
        return entityManager.createQuery("""
                        select p from Patient p
                        join fetch p.user
                        left join fetch p.allergies
                        where p.id = :id
                        """, Patient.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Patient> findByNationalId(String nationalId) {
        return entityManager.createQuery("""
                        select p from Patient p
                        join fetch p.user u
                        left join fetch p.allergies
                        where u.nationalId = :nationalId
                        """, Patient.class)
                .setParameter("nationalId", nationalId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Page<Patient> search(String query, Gender gender, BloodType bloodType, Pageable pageable) {
        List<Patient> content = bindSearchParameters(
                entityManager.createQuery(
                        "select p from Patient p join fetch p.user u " + SEARCH_CONDITIONS + orderBy(pageable.getSort()),
                        Patient.class),
                query, gender, bloodType)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = bindSearchParameters(
                entityManager.createQuery(
                        "select count(p) from Patient p join p.user u " + SEARCH_CONDITIONS,
                        Long.class),
                query, gender, bloodType)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private <T> TypedQuery<T> bindSearchParameters(TypedQuery<T> typedQuery, String query,
                                                   Gender gender, BloodType bloodType) {
        return typedQuery
                .setParameter("query", query)
                .setParameter("gender", gender)
                .setParameter("bloodType", bloodType);
    }

    private String orderBy(Sort sort) {
        if (sort.isUnsorted()) {
            return " order by u.surname";
        }
        return sort.stream()
                .map(order -> {
                    String field = SORTABLE_FIELDS.get(order.getProperty());
                    if (field == null) {
                        throw new IllegalArgumentException("Unsupported sort field: " + order.getProperty());
                    }
                    return field + (order.isAscending() ? " asc" : " desc");
                })
                .collect(Collectors.joining(", ", " order by ", ""));
    }
}