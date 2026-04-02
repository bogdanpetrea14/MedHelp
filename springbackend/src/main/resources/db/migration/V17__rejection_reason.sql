-- Motiv de respingere pentru cererile de activare
ALTER TABLE medconnect.users
    ADD COLUMN IF NOT EXISTS rejection_reason TEXT;