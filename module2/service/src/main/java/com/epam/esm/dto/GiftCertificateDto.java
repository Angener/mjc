package com.epam.esm.dto;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCertificateDto {
    GiftCertificate giftCertificate;
    String[] fields;
    List<Tag> tags;
}
