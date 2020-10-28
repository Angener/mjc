package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.util.BeanFactory;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class TagServiceImpl implements TagService{
    @Autowired
    TagDao tagDao;

    @Autowired
    BeanFactory beanFactory;

    @Override
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    public Tag get(String name) {
        Preconditions.checkNotNull(name);
        return Preconditions.checkNotNull(tagDao.get(name));
    }

    @Override
    public void save(String name) {


    }

    @Override
    public void delete(Tag tag) {
        tagDao.delete(tag);
    }
}
