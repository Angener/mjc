package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TagServiceImpl implements TagService {
    TagDao tagDao;

    @Override
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    public Tag getById(long id) {
        return tagDao.getById(id);
    }

    @Override
    public Tag get(String name) {
        return tagDao.getByName(name);
    }

    @Override
    public long save(Tag tag) {
        return tagDao.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        tagDao.delete(tag);
    }
}
