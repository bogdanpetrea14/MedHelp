CREATE TABLE allergies (
                           id                  uuid                NOT NULL,
                           patient_id          uuid                NOT NULL,
                           active_substance_id uuid                NOT NULL,
                           severity            allergy_severity    NOT NULL,
                           notes               text,
                           PRIMARY KEY (id),
                           UNIQUE (patient_id, active_substance_id),
                           FOREIGN KEY (patient_id)          REFERENCES medconnect.patients (id)          ON DELETE CASCADE,
                           FOREIGN KEY (active_substance_id) REFERENCES medconnect.active_substances (id) ON DELETE RESTRICT
);