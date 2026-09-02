CREATE TABLE IF NOT EXISTS clinic_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    decimal_value DECIMAL(12,2) NOT NULL,
    CONSTRAINT chk_setting_decimal_value CHECK (decimal_value >= 0)
);

INSERT INTO clinic_settings (setting_key, decimal_value)
VALUES ('consultation_fee', 1000.00)
ON CONFLICT (setting_key) DO NOTHING;

ALTER TABLE bills ADD COLUMN bill_number VARCHAR(20);
UPDATE bills SET bill_number = 'BILL-' || LPAD(bill_id::TEXT, 8, '0') WHERE bill_number IS NULL;
ALTER TABLE bills ALTER COLUMN bill_number SET NOT NULL;
ALTER TABLE bills ADD CONSTRAINT uq_bills_bill_number UNIQUE (bill_number);
