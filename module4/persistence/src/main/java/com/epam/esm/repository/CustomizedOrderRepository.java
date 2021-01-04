package com.epam.esm.repository;

import com.epam.esm.entity.MostWidelyUsedTag;

public interface CustomizedOrderRepository {
    MostWidelyUsedTag findMostWidelyUsedTag(int userId);
}
