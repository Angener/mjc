CREATE SCHEMA IF NOT EXISTS giftCertificate;
USE giftCertificate;
CREATE TABLE IF NOT EXISTS tag(
	id BIGSERIAL PRIMARY KEY NOT NULL,
    name CHARACTER VARYING(255) UNIQUE NOT NULL);
CREATE TABLE IF NOT EXISTS giftCertificate(
	id BIGSERIAL PRIMARY KEY NOT NULL,
    name CHARACTER VARYING(255) UNIQUE NOT NULL,
    description CHARACTER VARYING(1000) NOT NULL DEFAULT 'empty',
    price MONEY NOT NULL DEFAULT 0.0,
    createDate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastUpdateDate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration INTEGER NOT NULL DEFAULT 1
	);
CREATE TABLE IF NOT EXISTS tag_giftCertificate(
    tag_id BIGINT,
    giftCertificate_id BIGINT,
    PRIMARY KEY (tag_id, giftCertificate_id),
    CONSTRAINT FK_tag_id FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_giftCertificate_id FOREIGN KEY (giftCertificate_id)
    REFERENCES giftCertificate (id) ON DELETE CASCADE ON UPDATE CASCADE);