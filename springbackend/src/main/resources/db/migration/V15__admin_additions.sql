-- Adăugare status INACTIVE pentru suspendarea conturilor
ALTER TYPE medconnect.user_status ADD VALUE IF NOT EXISTS 'INACTIVE';

-- Câmp unitate medicală pentru doctori
ALTER TABLE medconnect.doctors
    ADD COLUMN IF NOT EXISTS medical_unit TEXT;

-- Câmpuri CUI și licență funcționare pentru farmacii
ALTER TABLE medconnect.pharmacies
    ADD COLUMN IF NOT EXISTS cui TEXT UNIQUE,
    ADD COLUMN IF NOT EXISTS pharmacy_license TEXT;