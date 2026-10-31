CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE availabilities
    ADD CONSTRAINT ex_availabilities_clinician_no_overlap
    EXCLUDE USING gist (
        clinician_id WITH =,
        tsrange(work_date + start_time, work_date + end_time) WITH &&
    ) WHERE (status <> 'REJECTED');

ALTER TABLE appointments
    ADD CONSTRAINT ex_appointments_clinician_no_overlap
    EXCLUDE USING gist (
        clinician_id WITH =,
        tsrange(appointment_date + start_time, appointment_date + end_time) WITH &&
    ) WHERE (status <> 'CANCELLED');

ALTER TABLE appointments
    ADD CONSTRAINT ex_appointments_patient_no_overlap
    EXCLUDE USING gist (
        patient_id WITH =,
        tsrange(appointment_date + start_time, appointment_date + end_time) WITH &&
    ) WHERE (status <> 'CANCELLED');