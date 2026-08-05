CREATE TABLE t1_countries (
    numeric_code SMALLINT     NOT NULL,
    alpha3_code  VARCHAR(3)   NOT NULL,
    name         VARCHAR(100) NOT NULL,
    CONSTRAINT pk_t1 PRIMARY KEY (numeric_code),
    CONSTRAINT uk_t1_alpha3 UNIQUE (alpha3_code)
);

CREATE TABLE t2_currencies (
    numeric_code SMALLINT     NOT NULL,
    alpha3_code  VARCHAR(3)   NOT NULL,
    name         VARCHAR(100) NOT NULL,
    symbol       VARCHAR(8)   NOT NULL,
    CONSTRAINT pk_t2 PRIMARY KEY (numeric_code),
    CONSTRAINT uk_t2_alpha3 UNIQUE (alpha3_code)
);

CREATE TABLE t3_country_currency (
    country_numeric_code   SMALLINT NOT NULL,
    currency_numeric_code  SMALLINT NOT NULL,
    CONSTRAINT pk_t3 PRIMARY KEY (country_numeric_code, currency_numeric_code),
    CONSTRAINT fk_t3_t1 FOREIGN KEY (country_numeric_code) REFERENCES t1_countries (numeric_code),
    CONSTRAINT fk_t3_t2 FOREIGN KEY (currency_numeric_code) REFERENCES t2_currencies (numeric_code)
);

INSERT INTO t1_countries (numeric_code, alpha3_code, name) VALUES
    (724, 'ESP', 'Spain'),
    (826, 'GBR', 'United Kingdom'),
    (840, 'USA', 'United States'),
    (152, 'CHL', 'Chile'),
    (702, 'SGP', 'Singapore'),
    (36, 'AUS', 'Australia'),
    (710, 'ZAF', 'South Africa');

INSERT INTO t2_currencies (numeric_code, alpha3_code, name, symbol) VALUES
    (978, 'EUR', 'Euro', '€'),
    (826, 'GBP', 'Pound Sterling', '£'),
    (840, 'USD', 'US Dollar', '$'),
    (152, 'CLP', 'Chilean Peso', '$'),
    (990, 'CLF', 'Unidad de Fomento', 'UF'),
    (702, 'SGD', 'Singapore Dollar', '$'),
    (36, 'AUD', 'Australian Dollar', '$'),
    (710, 'ZAR', 'Rand', 'R');

INSERT INTO t3_country_currency (country_numeric_code, currency_numeric_code) VALUES
    (724, 978),
    (826, 826),
    (840, 840),
    (152, 152),
    (152, 990),
    (702, 702),
    (36, 36),
    (710, 710);
