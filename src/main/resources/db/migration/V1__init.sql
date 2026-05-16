CREATE TABLE practitioners (
    id UUID PRIMARY KEY,
    full_name TEXT NOT NULL,
    crm TEXT NOT NULL UNIQUE,
    specialty TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bank_accounts (
    id UUID PRIMARY KEY,
    practitioner_id UUID NOT NULL REFERENCES practitioners(id),
    bank_code TEXT NOT NULL,
    agency TEXT NOT NULL,
    account_number TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bank_accounts_practitioner ON bank_accounts(practitioner_id);

CREATE TABLE earnings (
    id UUID PRIMARY KEY,
    practitioner_id UUID NOT NULL REFERENCES practitioners(id),
    amount NUMERIC(12, 2) NOT NULL,
    competency_month TEXT NOT NULL,
    notes TEXT,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_earnings_practitioner ON earnings(practitioner_id);

-- Seed
INSERT INTO practitioners (id, full_name, crm, specialty) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Dr. Carlos Mendes', 'CRM-SP 12345', 'Cardiologia'),
  ('22222222-2222-2222-2222-222222222222', 'Dra. Marina Souza', 'CRM-SP 67890', 'Dermatologia');

INSERT INTO bank_accounts (id, practitioner_id, bank_code, agency, account_number) VALUES
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111',
   '341', '0001', '12345-6'),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222',
   '237', '0042', '98765-4');

INSERT INTO earnings (id, practitioner_id, amount, competency_month, notes, updated_by) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
   5000.00, '2026-10', 'Repasse mensal',
   '99999999-9999-9999-9999-999999999999');
