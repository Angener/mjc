package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class TagDaoImpl implements TagDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> getAll() {
        return (List<Tag>) entityManager.createQuery("FROM Tag").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tag> getAll(int startPosition, int recordsQuantity) {
        return (List<Tag>) entityManager.createQuery("FROM Tag")
                .setFirstResult(startPosition)
                .setMaxResults(recordsQuantity)
                .getResultList();
    }

    @Override
    public Tag getById(int id) {
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
    @SuppressWarnings("unchecked")
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return (List<Tag>) entityManager.createQuery(
                "SELECT tag From Tag tag JOIN tag.giftCertificates g WHERE g.id = :id")
                .setParameter("id", certificate.getId())
                .getResultList();
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        entityManager.persist(tag);
        return tag;
    }

    @Override
    @Transactional
    public void delete(Tag deletableTag) {
        Tag tag = entityManager.find(Tag.class, deletableTag.getId());
        tag.getGiftCertificates().forEach(certificate -> certificate.getTags().remove(tag));
        entityManager.remove(tag);
    }

    @Override
    public long getTagsQuantity() {
        return (long) entityManager.createQuery("SELECT COUNT(*) FROM Tag").getSingleResult();
    }
}
