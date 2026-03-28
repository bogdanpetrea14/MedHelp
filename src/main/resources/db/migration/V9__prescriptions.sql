CREATE TABLE prescriptions (
                               id              uuid                    NOT NULL,
                               doctor_id       uuid                    NOT NULL,
                               patient_id      uuid                    NOT NULL,
                               unique_code     text                    NOT NULL UNIQUE,
                               status          prescription_status     NOT NULL DEFAULT 'PRESCRIBED',
                               prescribed_at   timestamp               NOT NULL DEFAULT NOW(),
                               picked_up_at    timestamp,
                               doctor_notes    text,
                               PRIMARY KEY (id),
                               FOREIGN KEY (doctor_id)  REFERENCES medconnect.doctors (id)  ON DELETE RESTRICT,
                               FOREIGN KEY (patient_id) REFERENCES medconnect.patients (id) ON DELETE RESTRICT
);