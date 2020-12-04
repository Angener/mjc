package com.epam.esm.dao;

import com.epam.esm.config.Config;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.sql.SQLException;

@SpringJUnitWebConfig(Config.class)
@ContextConfiguration(
        classes = {Config.class},
        loader = AnnotationConfigContextLoader.class)
public class InMemoryDbConfig {
    private static final String CREATE_TAG_TABLE = "CREATE TABLE IF NOT EXISTS tag (" +
            "id BIGSERIAL PRIMARY KEY NOT NULL, " +
            "name CHARACTER VARYING(255) UNIQUE NOT NULL);";
    private static final String CREATE_GIFT_CERTIFICATE_TABLE = "CREATE TABLE IF NOT EXISTS gift_certificate (" +
            "id BIGSERIAL PRIMARY KEY NOT NULL, " +
            "name CHARACTER VARYING(255) UNIQUE NOT NULL, " +
            "description CHARACTER VARYING(1000) NOT NULL DEFAULT 'empty', " +
            "price MONEY NOT NULL DEFAULT 0.0, " +
            "create_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "last_update_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "duration INTEGER NOT NULL DEFAULT 1);";
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS \"user\" (" +
            "id BIGSERIAL PRIMARY KEY NOT NULL, " +
            "name CHARACTER VARYING(255) UNIQUE NOT NULL);";
    private static final String CREATE_TAG_GIFT_CERTIFICATE_TABLE = "CREATE TABLE IF NOT EXISTS tag_gift_certificate (" +
            "tag_id BIGINT, " +
            "gift_certificate_id BIGINT, " +
            "PRIMARY KEY (tag_id, gift_certificate_id), " +
            "CONSTRAINT FK_tag_id FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "CONSTRAINT FK_gift_certificate_id FOREIGN KEY (gift_certificate_id) " +
            "REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE);";
    private static final String FILL_TAG_TABLE = "INSERT INTO TAG (name) VALUES " +
            "('first tag'), " +
            "('second tag');";
    private static final String FILL_GIFT_CERTIFICATE_TABLE = "INSERT INTO gift_certificate " +
            "(name, description, price, duration) VALUES (" +
            "'first', 'first gift card', 123.2, 12), " +
            "('second', 'second gift card', 3.2, 2), " +
            "('third', 'third gift card', 3.2, 4), " +
            "('fourth', 'fourth gift card', 3.2, 12), " +
            "('fifth', 'fifth gift card', 3.2, 22);";
    private static final String FILL_TAG_GIFT_CERTIFICATE_TABLE = "INSERT INTO tag_gift_certificate " +
            "(tag_id, gift_certificate_id) VALUES (1, 1), (2, 1), (1, 2), (1, 3), (1, 4), (1, 5);";
    private static final String FILL_USER_TABLE = "INSERT INTO \"user\" (name) VALUES " +
            "('user1'), " +
            "('user2');";
    private static final String DROP_TAG_TABLE = "DROP TABLE tag;";
    private static final String DROP_GIFT_CERTIFICATE_TABLE = "DROP TABLE gift_certificate;";
    private static final String DROP_TAG_GIFT_CERTIFICATE_TABLE = "DROP TABLE tag_gift_certificate;";
    private static final String DROP_USER_TABLE = "DROP TABLE \"user\"";

    @Autowired
    JdbcTemplate jdbcTemplate;

    void setUp() throws SQLException {
        jdbcTemplate.execute(getInitSqlScript());
    }

    private String getInitSqlScript() {
        return String.join(" ",
                CREATE_TAG_TABLE,
                CREATE_GIFT_CERTIFICATE_TABLE,
                CREATE_USER_TABLE,
                CREATE_TAG_GIFT_CERTIFICATE_TABLE,
                FILL_TAG_TABLE,
                FILL_GIFT_CERTIFICATE_TABLE,
                FILL_TAG_GIFT_CERTIFICATE_TABLE,
                FILL_USER_TABLE);
    }

    void destroy() {
        jdbcTemplate.execute(getDestroySqlScript());
    }

    private String getDestroySqlScript() {
        return String.join(" ",
                DROP_TAG_GIFT_CERTIFICATE_TABLE,
                DROP_TAG_TABLE,
                DROP_GIFT_CERTIFICATE_TABLE,
                DROP_USER_TABLE);
    }
}
