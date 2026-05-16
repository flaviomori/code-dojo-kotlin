CREATE TABLE payment_splits (
    id UUID PRIMARY KEY,
    practitioner_id UUID NOT NULL REFERENCES practitioners(id),
    bank_account_id UUID NOT NULL REFERENCES bank_accounts(id),
    share_percentage NUMERIC(5, 2) NOT NULL,
    procedure_ref TEXT,
    notes TEXT,
    registered_by UUID NOT NULL,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_splits_practitioner ON payment_splits(practitioner_id);
CREATE INDEX idx_payment_splits_bank_account ON payment_splits(bank_account_id);

-- Seed: split do repasse de Dr. Carlos para sua conta principal
INSERT INTO payment_splits (id, practitioner_id, bank_account_id, share_percentage, procedure_ref, registered_by, updated_by) VALUES
  ('cccccccc-cccc-cccc-cccc-cccccccccccc',
   '11111111-1111-1111-1111-111111111111',
   'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   60.00, 'Cirurgia cardiovascular #2026-10-014',
   '99999999-9999-9999-9999-999999999999',
   '99999999-9999-9999-9999-999999999999');
