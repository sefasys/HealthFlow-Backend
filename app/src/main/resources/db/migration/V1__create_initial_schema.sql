CREATE TABLE user(
                     unique_id UUID PRIMARY KEY,
                     national_id   VARCHAR(11) NOT NULL UNIQUE,
                     name VARCHAR(50) NOT NULL,
                     surname VARCHAR(50) NOT NULL,
                     birth_date DATE,
                     email VARCHAR(255) NOT NULL,
                     phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE patient(
                        user_id UUID PRIMARY KEY NOT NULL REFERENCES user(unique_id),
                        blood_type VARCHAR(10)
);

CREATE TABLE staff(
                      user_id UUID PRIMARY KEY  NOT NULL REFERENCES user(unique_id),
                      employee_id UUID NOT NULL UNIQUE,
                      hire_date DATE NOT NULL,
                      employment_status VARCHAR(30) NOT NULL
);

CREATE TABLE clinician(
                          user_id UUID PRIMARY KEY NOT NULL REFERENCES staff(user_id)
);

CREATE TABLE clinic_registrar(
                                 user_id UUID PRIMARY KEY NOT NULL REFERENCES staff(user_id)
);

CREATE TABLE availability(
                             availability_id UUID PRIMARY KEY NOT NULL, /* Önce veri tipi sonra kurallar. */
                             clinician_user_id UUID NOT NULL REFERENCES clinician(user_id), /* Burada Unique Keyword'ü kullanmaya gerek yok. foreign key ama unique değil. */
                             date DATE NOT NULL,
                             start_time TIME NOT NULL,
                             end_time TIME NOT NULL,
                             status VARCHAR(50) NOT NULL,
                             reviewed_by_user_id UUID REFERENCES clinic_registrar(user_id),
                             reviewed_at TIMESTAMP,
                             rejection_reason VARCHAR(150),
                             CONSTRAINT check_availability_time CHECK ( start_time < end_time )
);

CREATE TABLE appointment(
                            appointment_id UUID PRIMARY KEY NOT NULL,
                            patient_user_id UUID REFERENCES patient(user_id), /* Bu attribute'larda neden PRIMARY KEY kullanalım ki? */
                            clinician_user_id UUID NOT NULL REFERENCES clinician(user_id),
                            availability_id UUID NOT NULL REFERENCES availability(availability_id),
                            date DATE NOT NULL,
                            start_time TIME NOT NULL,
                            end_time TIME NOT NULL,
                            status VARCHAR(100) NOT NULL,
                            CONSTRAINT check_appointment_time
                                CHECK (start_time < end_time)
);

CREATE INDEX idx_availability_clinician_date
    ON availability (clinician_user_id, date);

CREATE INDEX idx_appointment_clinician_date_status
    ON appointment (clinician_user_id, date, status);

CREATE INDEX idx_appointment_patient_date
    ON appointment (patient_user_id, date);

