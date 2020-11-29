package com.epam.esm.service;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao dao;

    @Autowired
    public UserServiceImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return dao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll(int page, int recordsPerPage) {
        return recordsPerPage > 0 ? getPaginateUsers(page, recordsPerPage) : getAll();
    }

    private List<User> getPaginateUsers(int page, int recordsPerPage) {
        int startPosition = getStartPosition(page, recordsPerPage);
        int recordsQuantity = getRecordsQuantity(startPosition, recordsPerPage);
        return checkResultUsersList(dao.getAll(startPosition, recordsQuantity));
    }

    private int getStartPosition(int page, int recordsPerPage) {
        return (page == 0) ? (0) : (page * recordsPerPage);
    }

    private int getRecordsQuantity(int startPosition, int recordsPerPage) {
        int menuSize = (int) dao.getUsersQuantity() - startPosition;
        int recordsQuantity = Math.min(recordsPerPage, menuSize);
        Preconditions.checkArgument(recordsQuantity > 0);
        return recordsQuantity;
    }

    private List<User> checkResultUsersList(List<User> users) {
        Preconditions.checkArgument(users.size() > 0);
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(int id) {
        return dao.getById(id);
    }
}
