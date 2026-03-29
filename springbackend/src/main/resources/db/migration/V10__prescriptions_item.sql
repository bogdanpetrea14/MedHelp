CREATE TABLE prescription_items (
                                    id                  uuid    NOT NULL,
                                    prescription_id     uuid    NOT NULL,
                                    active_substance_id uuid    NOT NULL,
                                    dose                text    NOT NULL,
                                    frequency           text    NOT NULL,
                                    duration_days       integer NOT NULL,
                                    notes               text,
                                    PRIMARY KEY (id),
                                    FOREIGN KEY (prescription_id)     REFERENCES medconnect.prescriptions (id)      ON DELETE CASCADE,
                                    FOREIGN KEY (active_substance_id) REFERENCES medconnect.active_substances (id)  ON DELETE RESTRICT
);