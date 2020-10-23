package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagDao {
     List<Tag> getAll ();
     Tag get(String name);
     void save(Tag tag);
     void delete(Tag t);
}
