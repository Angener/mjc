package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagDaoImpl implements TagDao {
    static String GET_ALL_CERTIFICATE_TAGS =
            "SELECT tag.id, tag.name FROM tag " +
                    "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                    "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                    "WHERE gift_certificate.id = :id;";
    static RowMapper<Tag> mapper = (rs, mapRow) -> new Tag(rs.getLong("id"),
            rs.getString("name"));
    DaoHelper daoHelper;
    EntityManager entityManager;

    @Autowired
    public TagDaoImpl(DaoHelper daoHelper, EntityManager entityManager) {
        this.daoHelper = daoHelper;
        this.entityManager=entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> getAll() {
        return (List<Tag>) entityManager.createQuery("from Tag").getResultList();
    }

    @Override
    public Tag getById(long id) {
        Tag tag = entityManager.find(Tag.class, id);
        entityManager.detach(tag);
        return tag;
    }

    @Override
    public Tag getByName(String name) {
        Tag tag = (Tag) entityManager.createQuery("FROM Tag t WHERE t.name = :name")
                .setParameter("name", name)
                .getSingleResult();
        entityManager.detach(tag);
        return tag;
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return daoHelper.getAllEntitiesFromTableReferencedEntity(GET_ALL_CERTIFICATE_TAGS, certificate, mapper);
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        entityManager.getTransaction().begin();
        entityManager.persist(tag);
        entityManager.getTransaction().commit();
        return tag;
    }

    @Override
    public void delete(Tag tag) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(Tag.class, tag.getId()));
        entityManager.getTransaction().commit();
    }
}
