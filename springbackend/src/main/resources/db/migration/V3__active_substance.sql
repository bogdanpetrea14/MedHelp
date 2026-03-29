CREATE TABLE active_substances (
                                   id          uuid    NOT NULL,
                                   name        text    NOT NULL UNIQUE,
                                   description text,
                                   category    text    NOT NULL,
                                   PRIMARY KEY (id)
);