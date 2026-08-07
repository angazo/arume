CREATE TABLE es3_legal_forms (
    code                  VARCHAR(100) NOT NULL,
    country_numeric_code  SMALLINT     NOT NULL,
    description           VARCHAR(255) NOT NULL,
    is_legal_person       BOOLEAN      NOT NULL,
    CONSTRAINT pk_es3 PRIMARY KEY (code, country_numeric_code),
    CONSTRAINT fk_es3_t1 FOREIGN KEY (country_numeric_code) REFERENCES t1_countries (numeric_code)
);

INSERT INTO es3_legal_forms (code, country_numeric_code, description, is_legal_person) VALUES
    ('EI',    724, 'Empresario individual',                FALSE),
    ('PA',    724, 'Profesional autónomo',                 FALSE),
    ('ERL',   724, 'Emprendedor de Responsabilidad Limitada', FALSE),
    ('SL',    724, 'Sociedad Limitada',                    TRUE),
    ('SLU',   724, 'Sociedad Limitada Unipersonal',        TRUE),
    ('SA',    724, 'Sociedad Anónima',                     TRUE),
    ('SAU',   724, 'Sociedad Anónima Unipersonal',         TRUE),
    ('SColl', 724, 'Sociedad Colectiva',                   TRUE),
    ('SCom',  724, 'Sociedad Comanditaria Simple',         TRUE),
    ('SComA', 724, 'Sociedad Comanditaria por Acciones',   TRUE),
    ('SCoop', 724, 'Sociedad Cooperativa',                 TRUE),
    ('SLL',   724, 'Sociedad Limitada Laboral',            TRUE),
    ('SAL',   724, 'Sociedad Anónima Laboral',             TRUE),
    ('SC',    724, 'Sociedad Civil',                       TRUE),
    ('CB',    724, 'Comunidad de Bienes',                  TRUE),
    ('AIE',   724, 'Agrupación de Interés Económico',      TRUE),
    ('SAT',   724, 'Sociedad Agraria de Transformación',   TRUE);
