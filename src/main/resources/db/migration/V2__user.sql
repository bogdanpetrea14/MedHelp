CREATE TABLE users (
                       id          uuid            NOT NULL,
                       email       text            NOT NULL UNIQUE,
                       password    text            NOT NULL,
                       role        user_role       NOT NULL,
                       status      user_status     NOT NULL DEFAULT 'ACTIVE',
                       created_at  timestamp       NOT NULL DEFAULT NOW(),
                       PRIMARY KEY (id)
);

-- Doctorii si farmaciile incep cu PENDING
-- Pacientii si adminii incep cu ACTIVE
-- Logica de DEFAULT se va gestiona in backend la register