-- The United Kingdom module owns no table of its own: it only seeds the jurisdiction data
-- of Great Britain into the core catalogs.

INSERT INTO t5_legal_forms (country_alpha2_code, code, description, is_organization) VALUES
    ('GB', 'ST',  'Sole Trader',                     FALSE),
    ('GB', 'PS', 'Partnership',                     TRUE),
    ('GB', 'LLP',         'Limited Liability Partnership',   TRUE),
    ('GB', 'Ltd',         'Private Limited Company',         TRUE),
    ('GB', 'PLC',         'Public Limited Company',          TRUE),
    ('GB', 'CLG',         'Company Limited by Guarantee',    TRUE),
    ('GB', 'CIC',         'Community Interest Company',      TRUE);

INSERT INTO t4_country_currency (country_alpha2_code, currency_numeric_code) VALUES
    ('GB', 826);
