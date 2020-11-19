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
import javax.persistence.EntityManagerFactory;
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
    EntityManagerFactory emf;

    @Autowired
    public TagDaoImpl(DaoHelper daoHelper, EntityManagerFactory emf) {
        this.daoHelper = daoHelper;
        this.emf=emf;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> getAll() {
        EntityManager em = emf.createEntityManager();
        List<Tag> list = (List<Tag>) em.createQuery("from Tag").getResultList();
        em.close();
        return list;
    }

    @Override
    public Tag getById(long id) {
        EntityManager em = emf.createEntityManager();
        Tag tag = em.find(Tag.class, id);
        em.detach(tag);
        em.close();
        return tag;
    }

    @Override
    public Tag getByName(String name) {
        EntityManager em = emf.createEntityManager();
        Tag tag = (Tag) em.createQuery("FROM Tag t WHERE t.name = :name")
                .setParameter("name", name)
                .getSingleResult();
        em.detach(tag);
        em.close();
        return tag;
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return daoHelper.getAllEntitiesFromTableReferencedEntity(GET_ALL_CERTIFICATE_TAGS, certificate, mapper);
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(tag);
        em.getTransaction().commit();
        em.close();
        return tag;
    }

    @Override
    public void delete(Tag tag) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(Tag.class, tag.getId()));
        em.getTransaction().commit();
        em.close();
    }
}
