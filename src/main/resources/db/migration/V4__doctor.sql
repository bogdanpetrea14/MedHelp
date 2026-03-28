CREATE TABLE doctors (
                         id              uuid    NOT NULL,
                         user_id         uuid    NOT NULL UNIQUE,
                         first_name      text    NOT NULL,
                         last_name       text    NOT NULL,
                         speciality      text    NOT NULL,
                         license_number  text    NOT NULL UNIQUE,
                         PRIMARY KEY (id),
                         FOREIGN KEY (user_id) REFERENCES medconnect.users (id) ON DELETE CASCADE
);