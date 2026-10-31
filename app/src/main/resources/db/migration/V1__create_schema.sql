CREATE TABLE users (
                       unique_id    UUID PRIMARY KEY,
                       national_id  VARCHAR(11)  NOT NULL UNIQUE,
                       name         VARCHAR(100) NOT NULL,
                       surname      VARCHAR(100) NOT NULL,
                       birth_date   DATE         NOT NULL,
                       email        VARCHAR(254) NOT NULL,
                       phone_number VARCHAR(32)  NOT NULL,
                       version      BIGINT       NOT NULL,
                       CONSTRAINT ck_users_national_id CHECK (national_id ~ '^[0-9]{10}[02468]$'),
    CONSTRAINT ck_users_name CHECK (btrim(name) <> '' AND btrim(surname) <> '')
);

CREATE TABLE user_roles (
                            user_id UUID         NOT NULL REFERENCES users (unique_id),
                            role    VARCHAR(255) NOT NULL,
                            PRIMARY KEY (user_id, role),
                            CONSTRAINT ck_user_roles_role
                                CHECK (role IN ('APPLICATION_ADMIN', 'CLINIC_REGISTRAR', 'CLINICIAN', 'PATIENT'))
);

CREATE TABLE patients (
                          user_id    UUID PRIMARY KEY REFERENCES users (unique_id),
                          blood_type VARCHAR(16) NOT NULL,
                          version    BIGINT      NOT NULL,
                          CONSTRAINT ck_patients_blood_type CHECK (blood_type IN (
                                                                                  'A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE',
                                                                                  'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE', 'UNKNOWN'))
);

CREATE TABLE staff (
                       user_id           UUID PRIMARY KEY REFERENCES users (unique_id),
                       employee_id       UUID        NOT NULL UNIQUE,
                       hire_date         DATE        NOT NULL,
                       employment_status VARCHAR(16) NOT NULL,
                       staff_type        VARCHAR(25) NOT NULL,
                       version           BIGINT      NOT NULL,
                       CONSTRAINT ck_staff_employment_status CHECK (employment_status IN
                                                                    ('ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'TERMINATED', 'INACTIVE')),
                       CONSTRAINT ck_staff_staff_type CHECK (staff_type IN ('CLINICIAN', 'CLINIC_REGISTRAR'))
);

CREATE TABLE availabilities (
                                unique_id        UUID PRIMARY KEY,
                                clinician_id     UUID        NOT NULL REFERENCES staff (user_id),
                                work_date        DATE        NOT NULL,
                                start_time       TIME        NOT NULL,
                                end_time         TIME        NOT NULL,
                                status           VARCHAR(16) NOT NULL,
                                reviewed_by      UUID REFERENCES staff (user_id),
                                reviewed_at      TIMESTAMPTZ,
                                rejection_reason VARCHAR(500),
                                version          BIGINT      NOT NULL,
                                CONSTRAINT ck_availabilities_time CHECK (start_time < end_time),
                                CONSTRAINT ck_availabilities_status
                                    CHECK (status IN ('PENDING_REVIEW', 'PUBLISHED', 'REJECTED')),
                                CONSTRAINT ck_availabilities_review CHECK (
                                    (status = 'PENDING_REVIEW' AND reviewed_by IS NULL AND reviewed_at IS NULL
                                        AND rejection_reason IS NULL)
                                        OR (status = 'PUBLISHED' AND reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL
                                        AND rejection_reason IS NULL)
                                        OR (status = 'REJECTED' AND reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL
                                        AND btrim(rejection_reason) <> ''))
);

CREATE TABLE appointments (
                              unique_id        UUID PRIMARY KEY,
                              availability_id  UUID        NOT NULL REFERENCES availabilities (unique_id),
                              patient_id       UUID        NOT NULL REFERENCES patients (user_id),
                              clinician_id     UUID        NOT NULL REFERENCES staff (user_id),
                              appointment_date DATE        NOT NULL,
                              start_time       TIME        NOT NULL,
                              end_time         TIME        NOT NULL,
                              status           VARCHAR(16) NOT NULL,
                              version          BIGINT      NOT NULL,
                              CONSTRAINT ck_appointments_time CHECK (start_time < end_time),
                              CONSTRAINT ck_appointments_slot CHECK (end_time - start_time = INTERVAL '15 minutes'),
    CONSTRAINT ck_appointments_participants CHECK (patient_id <> clinician_id),
    CONSTRAINT ck_appointments_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'))
);

CREATE INDEX idx_availabilities_clinician_date ON availabilities (clinician_id, work_date);
CREATE INDEX idx_availabilities_status ON availabilities (status);
CREATE INDEX idx_appointments_patient_date ON appointments (patient_id, appointment_date);
CREATE INDEX idx_appointments_clinician_date ON appointments (clinician_id, appointment_date);