package com.epam.esm.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SqlScript {

    SAVE_CERTIFICATE(
            "INSERT INTO gift_certificate (name, description, price, duration) " +
                    "VALUES (:name, :description, :price, :duration);"),
    GET_ALL_CERTIFICATES("SELECT * FROM gift_certificate;"),
    GET_CERTIFICATE_BY_ID("SELECT * FROM gift_certificate WHERE id = :param;"),
    GET_CERTIFICATE_BY_NAME("SELECT * FROM gift_certificate WHERE name = :param;"),
    GET_CERTIFICATES_BY_TAG_NAME(
            "SELECT gc.id, gc.name, gc.description, gc.price, " +
                    "gc.create_date, gc.last_update_date, gc.duration " +
                    "FROM gift_certificate gc " +
                    "JOIN tag_gift_certificate tgc ON gc.id = tgc.gift_certificate_id " +
                    "JOIN tag ON tag.id = tgc.tag_id " +
                    "WHERE tag.name= :param;"),
    GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION(
            "SELECT * FROM gift_certificate WHERE name LIKE :param " +
                    "OR description LIKE :param;"),
    UPDATE_CERTIFICATE(
            "UPDATE gift_certificate " +
                    "SET ?INSERT FIELDS?, " +
                    "last_update_date = CURRENT_TIMESTAMP " +
                    "WHERE id = :id;"),
    DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS("DELETE FROM tag_gift_certificate WHERE gift_certificate_id= :id;"),
    DELETE_CERTIFICATE("DELETE FROM gift_certificate WHERE id = :id;"),

    GET_ALL_TAGS("SELECT * FROM tag;"),
    GET_TAG_BY_ID("SELECT * FROM tag WHERE id = :param;"),
    GET_TAG_BY_NAME("SELECT * FROM tag WHERE name = :param;"),
    GET_ALL_CERTIFICATE_TAGS(
            "SELECT tag.id, tag.name FROM tag " +
                    "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                    "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                    "WHERE gift_certificate.id = :id;"),
    SAVE_TAG("INSERT INTO tag (name) VALUES (:name) ON CONFLICT DO NOTHING;"),
    DELETE_TAG("DELETE FROM tag WHERE id = :id;");

    String script;
}
