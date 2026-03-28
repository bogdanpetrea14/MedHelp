CREATE TABLE medications (
                             id                  uuid    NOT NULL,
                             active_substance_id uuid    NOT NULL,
                             brand_name          text    NOT NULL,
                             concentration       text    NOT NULL,
                             form                text    NOT NULL,
                             PRIMARY KEY (id),
                             FOREIGN KEY (active_substance_id) REFERENCES medconnect.active_substances (id) ON DELETE RESTRICT
);