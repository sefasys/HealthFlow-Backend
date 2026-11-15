package com.healthflow;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestBase {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE appointments, availabilities, clinicians, clinic_registrars,
                           staff, patient_allergies, patients, user_roles, users
            CASCADE
            """);
    }

    protected Statistics statistics() {
        return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    protected int countRows(String table) {
        return JdbcTestUtils.countRowsInTable(jdbcTemplate, table);
    }

    protected int countRowsWhere(String table, String whereClause) {
        return JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, table, whereClause);
    }
}