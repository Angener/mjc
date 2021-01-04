package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Integer> {

    @Query("SELECT DISTINCT c FROM GiftCertificate c JOIN c.tags tag WHERE tag.name IN :tags " +
            "GROUP BY c.id HAVING COUNT(c) >= :count")
    Page<GiftCertificate> findByTagName(@Param("tags") Set<String> tagNames,
                                        @Param("count") long tagsQuantity,
                                        Pageable pageable);

    @Query("FROM GiftCertificate c WHERE c.name LIKE CONCAT('%',:text,'%') OR c.description LIKE CONCAT('%',:text,'%')")
    Page<GiftCertificate> findDistinctByNameLikeOrDescriptionLike(@Param("text") String text, Pageable pageable);

    @Query("SELECT DISTINCT c FROM GiftCertificate c JOIN c.tags tag WHERE tag.name IN :tags " +
            "OR c.name LIKE CONCAT ('%',:text,'%') OR c.description LIKE CONCAT ('%',:text,'%')" +
            "GROUP BY c.id HAVING COUNT(c) >= :count")
    Page<GiftCertificate> findByTagAndPartNameOrDescription(@Param("tags") Set<String> tagNames,
                                                            @Param("count") long tagsQuantity,
                                                            @Param("text") String text,
                                                            Pageable pageable);
}
