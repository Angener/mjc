package com.epam.esm.dao;

import com.epam.esm.entity.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDaoImpl implements UserDao {
    static String GET_ALL_USERS = "SELECT * FROM \"user\";";
    static String GET_USER_BY_ID = "SELECT * FROM \"user\" WHERE id = :param;";
    static String GET_USER_BY_NAME = "SELECT * FROM \"user\" WHERE name = :param;";
    static RowMapper<User> mapper = (rs, mapRow) -> new User(rs.getLong("id"),
            rs.getString("name"));
    DaoHelper daoHelper;

    @Autowired
    public UserDaoImpl(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    @Override
    public List<User> getAll() {
        return daoHelper.getAllEntityFromTable(GET_ALL_USERS, mapper);
    }

    @Override
    public User getByName(String name) {
        return daoHelper.getEntityFromTable(GET_USER_BY_NAME, name, mapper);
    }

    @Override
    public User getById(long id) {
        return daoHelper.getEntityFromTable(GET_USER_BY_ID, id, mapper);
    }
}
