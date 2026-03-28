CREATE TABLE pharmacies (
                            id          uuid    NOT NULL,
                            user_id     uuid    NOT NULL UNIQUE,
                            name        text    NOT NULL,
                            address     text    NOT NULL,
                            latitude    float   NOT NULL,
                            longitude   float   NOT NULL,
                            PRIMARY KEY (id),
                            FOREIGN KEY (user_id) REFERENCES medconnect.users (id) ON DELETE CASCADE
);