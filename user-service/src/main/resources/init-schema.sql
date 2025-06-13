DROP SCHEMA IF EXISTS "user" CASCADE;

CREATE SCHEMA "user";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user".users
(
    id uuid NOT NULL,
    username character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

DROP MATERIALIZED VIEW IF EXISTS "user".order_user_m_view;

CREATE MATERIALIZED VIEW "user".order_user_m_view
TABLESPACE pg_default
AS
 SELECT id,
    username,
    first_name,
    last_name
   FROM "user".users
WITH DATA;

refresh materialized VIEW "user".order_user_m_view;

DROP function IF EXISTS "user".refresh_order_user_m_view;

CREATE OR replace function "user".refresh_order_user_m_view()
returns trigger
AS '
BEGIN
    refresh materialized VIEW "user".order_user_m_view;
    return null;
END;
'  LANGUAGE plpgsql;

DROP trigger IF EXISTS refresh_order_user_m_view ON "user".users;

CREATE trigger refresh_order_user_m_view
after INSERT OR UPDATE OR DELETE OR truncate
ON "user".users FOR each statement
EXECUTE PROCEDURE "user".refresh_order_user_m_view();