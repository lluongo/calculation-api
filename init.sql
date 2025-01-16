

CREATE TABLE call_history (
id SERIAL PRIMARY KEY,
timestamp TIMESTAMPTZ NOT NULL,
endpoint VARCHAR(255) NOT NULL,
parameters VARCHAR(255),
response VARCHAR(255),
error VARCHAR(255)
);