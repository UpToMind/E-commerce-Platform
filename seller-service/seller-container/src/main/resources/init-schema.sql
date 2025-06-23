DROP SCHEMA IF EXISTS seller CASCADE;

CREATE SCHEMA seller;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS seller.sellers CASCADE;

CREATE TABLE seller.sellers
(
    id uuid NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT sellers_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS approval_status;

CREATE TYPE approval_status AS ENUM ('APPROVED', 'REJECTED');

DROP TABLE IF EXISTS seller.order_approval CASCADE;

CREATE TABLE seller.order_approval
(
    id uuid NOT NULL,
    seller_id uuid NOT NULL,
    order_id uuid NOT NULL,
    status approval_status NOT NULL,
    CONSTRAINT order_approval_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS seller.products CASCADE;

CREATE TABLE seller.products
(
    id uuid NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    price numeric(10,2) NOT NULL,
    available boolean NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS seller.seller_products CASCADE;

CREATE TABLE seller.seller_products
(
    id uuid NOT NULL,
    seller_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT seller_products_pkey PRIMARY KEY (id)
);

ALTER TABLE seller.seller_products
    ADD CONSTRAINT "FK_SELLER_ID" FOREIGN KEY (seller_id)
    REFERENCES seller.sellers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT
    NOT VALID;

ALTER TABLE seller.seller_products
    ADD CONSTRAINT "FK_PRODUCT_ID" FOREIGN KEY (product_id)
    REFERENCES seller.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT
    NOT VALID;

DROP MATERIALIZED VIEW IF EXISTS seller.order_seller_m_view;

CREATE MATERIALIZED VIEW seller.order_seller_m_view
TABLESPACE pg_default
AS
 SELECT r.id AS seller_id,
    r.name AS seller_name,
    r.active AS seller_active,
    p.id AS product_id,
    p.name AS product_name,
    p.price AS product_price,
    p.available AS product_available
   FROM seller.sellers r,
    seller.products p,
    seller.seller_products rp
  WHERE r.id = rp.seller_id AND p.id = rp.product_id
WITH DATA;

refresh materialized VIEW seller.order_seller_m_view;

DROP function IF EXISTS seller.refresh_order_seller_m_view;

CREATE OR replace function seller.refresh_order_seller_m_view()
returns trigger
AS '
BEGIN
    refresh materialized VIEW seller.order_seller_m_view;
    return null;
END;
'  LANGUAGE plpgsql;

DROP trigger IF EXISTS refresh_order_seller_m_view ON seller.seller_products;

CREATE trigger refresh_order_seller_m_view
after INSERT OR UPDATE OR DELETE OR truncate
ON seller.seller_products FOR each statement
EXECUTE PROCEDURE seller.refresh_order_seller_m_view();