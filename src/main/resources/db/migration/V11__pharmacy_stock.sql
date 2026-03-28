CREATE TABLE pharmacy_stock (
                                id              uuid    NOT NULL,
                                pharmacy_id     uuid    NOT NULL,
                                medication_id   uuid    NOT NULL,
                                quantity        integer NOT NULL DEFAULT 0,
                                price           float   NOT NULL,
                                updated_at      timestamp NOT NULL DEFAULT NOW(),
                                PRIMARY KEY (id),
                                UNIQUE (pharmacy_id, medication_id),
                                FOREIGN KEY (pharmacy_id)  REFERENCES medconnect.pharmacies (id)  ON DELETE CASCADE,
                                FOREIGN KEY (medication_id) REFERENCES medconnect.medications (id) ON DELETE RESTRICT
);