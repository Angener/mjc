package com.epam.esm.repository.order;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.repository.CustomizedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;

public class CustomizedOrderRepositoryImpl implements CustomizedOrderRepository {

    private static final String GET_MOST_WIDELY_USED_TAG_SCRIPT =
            "SELECT tag.id AS tag_id, tag.name AS tag_name, MAX (o.order_cost) AS highest_cost " +
                    "FROM tag tag " +
                    "JOIN tag_gift_certificate tgc ON tgc.tag_id = tag.id " +
                    "JOIN \"order\" o ON o.certificate_id = tgc.gift_certificate_id " +
                    "WHERE o.user_id = :userId " +
                    "GROUP BY tag.id " +
                    "ORDER BY COUNT(tag.id) DESC " +
                    "LIMIT 1";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CustomizedOrderRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public MostWidelyUsedTag findMostWidelyUsedTag(int userId) {
        return namedParameterJdbcTemplate.queryForObject(
                GET_MOST_WIDELY_USED_TAG_SCRIPT,
                Collections.singletonMap("userId", userId),
                (rs, rowNum) -> new MostWidelyUsedTag(
                        rs.getInt("tag_id"),
                        rs.getString("tag_name"),
                        rs.getBigDecimal("highest_cost")));
    }
}
