CREATE TABLE patients (
                          id                  uuid    NOT NULL,
                          user_id             uuid    NOT NULL UNIQUE,
                          first_name          text    NOT NULL,
                          last_name           text    NOT NULL,
                          cnp                 text    NOT NULL UNIQUE,
                          birth_date          date    NOT NULL,
                          primary_doctor_id   uuid,
                          PRIMARY KEY (id),
                          FOREIGN KEY (user_id)           REFERENCES medconnect.users (id)   ON DELETE CASCADE,
                          FOREIGN KEY (primary_doctor_id) REFERENCES medconnect.doctors (id) ON DELETE SET NULL
);