package com.epam.esm.controller;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ContentLoaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentLoaderController.class);
    TagService tagService;
    GiftCertificateService giftCertificateService;
    UserService userService;
    OrderService orderService;

    @Autowired
    public ContentLoaderController(TagService tagService, GiftCertificateService giftCertificateService,
                                   UserService userService, OrderService orderService) {
        this.tagService = tagService;
        this.giftCertificateService = giftCertificateService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/loadContext")
    public String loadContext() {
        loadUsers();
        loadTags();
        loadCertificates();
        buyEachCertificate();
        return "Uploaded";
    }

    private void loadUsers() {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            try {
                userService.save(new User(faker.name().fullName()));
                LOGGER.info(i + " user created");
            } catch (Exception ex) {
                i--;
            }
        }
    }

    private void loadTags() {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            try {
                tagService.save(new Tag(faker.commerce().productName()));
                LOGGER.info(i + " tag created");
            } catch (Exception ex) {
                i--;
            }
        }
    }

    private void loadCertificates() {
        Faker faker = new Faker();
        List<Tag> tags = tagService.findAll(PageRequest.of(0, 100)).toList();

        for (int i = 0; i < 50; i++) {
            try {
                saveCertificate(tags, faker);
                LOGGER.info(i + " certificate created");
            } catch (Exception ex) {
                i--;
            }
        }
    }

    private void saveCertificate(List<Tag> tags, Faker faker) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName(faker.commerce().productName() + " " + faker.commerce().department() +
                " " + faker.commerce().material() + " " + faker.commerce().color());
        certificate.setDescription(faker.commerce().productName());
        certificate.setPrice(new BigDecimal(faker.commerce().price().replace(",", ".")));
        certificate.setDuration(faker.number().randomDigit());
        certificate.setTags(getRandomTagSet(tags, faker.number().numberBetween(1, 5), faker));
        giftCertificateService.save(certificate);
    }

    private Set<Tag> getRandomTagSet(List<Tag> tags, int maxQuantity, Faker faker) {
        Set<Tag> result = new HashSet<>();
        for (int i = 0; i < maxQuantity; i++) {
            result.add(tags.get(faker.number().numberBetween(0, 9)));
        }
        return result;
    }

    private void buyEachCertificate() {
        Faker faker = new Faker();
        List<GiftCertificate> certificates = giftCertificateService.findAll(PageRequest.of(0, 100)).toList();
        List<User> users = userService.findAll(PageRequest.of(0, 100)).toList();
        certificates.parallelStream().forEach(certificate -> {
            Order order = new Order();
            order.setCertificate(certificate);
            order.setUser(users.get(faker.number().numberBetween(0, users.size())));
            order = orderService.save(order);
            LOGGER.info(order.getId() + " order created");
        });
    }
}
