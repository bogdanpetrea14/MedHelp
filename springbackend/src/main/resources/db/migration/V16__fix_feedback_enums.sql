-- Actualizare tipuri enum pentru feedback ca să corespundă cu Java enums
-- Ștergem tabela și tipurile vechi, le recreăm cu valorile corecte

DROP TABLE IF EXISTS medconnect.feedback;

DROP TYPE IF EXISTS medconnect.feedback_category;
DROP TYPE IF EXISTS medconnect.feedback_rating;

CREATE TYPE medconnect.feedback_category AS ENUM (
    'APP_ISSUE',
    'PHARMACY_ISSUE',
    'DOCTOR_ISSUE',
    'GENERAL_SUGGESTION'
);

CREATE TYPE medconnect.feedback_rating AS ENUM (
    'ONE_STAR',
    'TWO_STARS',
    'THREE_STARS',
    'FOUR_STARS',
    'FIVE_STARS'
);

CREATE TABLE medconnect.feedback (
    id            UUID                       NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id       UUID                       NOT NULL REFERENCES medconnect.users(id) ON DELETE CASCADE,
    category      medconnect.feedback_category NOT NULL,
    rating        medconnect.feedback_rating   NOT NULL,
    allow_contact BOOLEAN                    NOT NULL DEFAULT FALSE,
    details       TEXT                       NOT NULL,
    created_at    TIMESTAMP                  NOT NULL DEFAULT NOW()
);