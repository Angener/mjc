package com.epam.esm.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SortCertificatesType {
    DATE_SORT("ORDER BY gc.create_date ASC"),
    NAME_SORT("ORDER BY gc.name ASC"),
    DATE_AND_NAME_SORT("ORDER BY gc.create_date ASC, gc.name ASC");

    String sortType;
}
