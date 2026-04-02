CREATE TABLE IF NOT EXISTS medconnect.prescription_cancellations
(
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    prescription_id UUID        NOT NULL REFERENCES medconnect.prescriptions (id) ON DELETE CASCADE,
    cancelled_by    UUID        NOT NULL REFERENCES medconnect.users (id),
    reason          TEXT        NOT NULL,
    cancelled_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);