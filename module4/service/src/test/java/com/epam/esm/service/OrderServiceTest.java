package com.epam.esm.service;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.service.order.OrderServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.NoResultException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    public void save() {
        Order order = new Order();
        when(repository.save(order)).thenReturn(order);
        assertEquals(order, service.save(order));
        verify(repository).save(order);
    }

    @Test
    public void findUserOrders() {
        when(repository.findAllByUserId(1, Pageable.unpaged())).thenReturn(Page.empty());

        assertEquals(Page.empty(), service.findUserOrders(1, Pageable.unpaged()));
        verify(repository).findAllByUserId(1, Pageable.unpaged());
    }

    @Test
    public void find() {
        when(repository.findByUserIdAndId(anyInt(), anyInt())).thenReturn(Optional.of(new Order()));

        assertEquals(service.find(anyInt(), anyInt()).getClass(), OrderDetail.class);
        verify(repository).findByUserIdAndId(anyInt(), anyInt());
    }

    @Test
    public void findThrowsNre() {
        when(repository.findByUserIdAndId(anyInt(), anyInt())).thenReturn(Optional.empty());
        assertThrows(NoResultException.class, () -> service.find(anyInt(), anyInt()));
    }

    @Test
    public void findMostWidelyUsedTag() {
        MostWidelyUsedTag tag = new MostWidelyUsedTag(1, "1", new BigDecimal("1"));
        when(repository.findMostWidelyUsedTag(anyInt())).thenReturn(tag);

        assertEquals(tag, service.findMostWidelyUsedTag(anyInt()));
        verify(repository).findMostWidelyUsedTag(anyInt());
    }
}
