package com.epam.esm.dao;

import com.epam.esm.config.JdbcConfig;
import com.epam.esm.entity.Tag;
import org.springframework.jdbc.core.JdbcTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringJUnitWebConfig(JdbcConfig.class)
@ContextConfiguration(
        classes = { JdbcConfig.class },
        loader = AnnotationConfigContextLoader.class)
public class Teeest {
    private static final String CREATE_TAG_TABLE = "CREATE TABLE IF NOT EXISTS tag (" +
            "id BIGSERIAL PRIMARY KEY NOT NULL, " +
            "name CHARACTER VARYING(255) UNIQUE NOT NULL);";
    private static final String CREATE_GIFT_CERTIFICATE_TABLE = "CREATE TABLE IF NOT EXISTS giftCertificate (" +
            "id BIGSERIAL PRIMARY KEY NOT NULL, " +
            "name CHARACTER VARYING(255) UNIQUE NOT NULL, " +
            "description CHARACTER VARYING(1000) NOT NULL DEFAULT 'empty', " +
            "price MONEY NOT NULL DEFAULT 0.0, " +
            "createDate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "lastUpdateDate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "duration INTEGER NOT NULL DEFAULT 1);";
    private static final String CREATE_TAG_GIFT_CERTIFICATE_TABLE = "CREATE TABLE IF NOT EXISTS tag_giftCertificate (" +
            "tag_id BIGINT, " +
            "giftCertificate_id BIGINT, " +
            "PRIMARY KEY (tag_id, giftCertificate_id), " +
            "CONSTRAINT FK_tag_id FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "CONSTRAINT FK_giftCertificate_id FOREIGN KEY (giftCertificate_id) " +
            "REFERENCES giftCertificate (id) ON DELETE CASCADE ON UPDATE CASCADE);";
    private static final String FILL_TAG_TABLE = "INSERT INTO TAG (name) VALUES " +
            "('first tag'), " +
            "('second tag');";
    private static final String FILL_GIFT_CERTIFICATE_TABLE = "INSERT INTO giftCertificate " +
            "(name, description, price, duration) VALUES (" +
            "'first', 'first gift card', 123.2, 12), " +
            "('second', 'second gift card', 3.2, 2), " +
            "('third', 'third gift card', 3.2, 4), " +
            "('fourth', 'fourth gift card', 3.2, 12), " +
            "('fifth', 'fifth gift card', 3.2, 22);";
    private static final String FILL_TAG_GIFT_CERTIFICATE_TABLE = "INSERT INTO tag_giftCertificate " +
            "(tag_id, giftCertificate_id) VALUES (1, 1), (2, 1), (1, 2), (1, 3), (1, 4), (1, 5);";

    @Autowired
    JdbcTemplate jdbcTemplate;
    DataSource dataSource;

    @BeforeEach
    public void init() throws SQLException {

        System.out.println("before test");
        String sqlScript = String.join(" ",
                CREATE_TAG_TABLE,
                CREATE_GIFT_CERTIFICATE_TABLE,
                CREATE_TAG_GIFT_CERTIFICATE_TABLE,
                FILL_TAG_TABLE,
                FILL_GIFT_CERTIFICATE_TABLE,
                FILL_TAG_GIFT_CERTIFICATE_TABLE);
        jdbcTemplate.execute(sqlScript);

        System.out.println("End befoore test");
    }

    @Test
    public void tre(){
        System.out.println("in test");
//        String sqlScript = String.join(" ", CREATE_DATABASE_SCRIPT, USE_DATABASE_SCRIPT,
//                CREATE_TAG_TABLE);
//        jdbcTemplate.execute(sqlScript);
        System.out.println(jdbcTemplate.query("SELECT * FROM tag", (rs, mapRow) -> new Tag(
                rs.getLong("id"),
                rs.getString("name"))
        ));

        }

//        System.out.println(jdbcTemplate.query("SELECT * FROM tag", (rs, mapRow) -> new Tag(
//                rs.getLong("id"),
//                rs.getString("name"))));

}
