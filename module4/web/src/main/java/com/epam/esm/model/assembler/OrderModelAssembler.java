package com.epam.esm.model.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.UserController;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.model.GiftCertificateModel;
import com.epam.esm.model.OrderModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler extends RepresentationModelAssemblerSupport<Order, OrderModel> {
    public OrderModelAssembler() {
        super(UserController.class, OrderModel.class);
    }

    @Override
    @NonNull
    public OrderModel toModel(@NonNull Order order) {
        OrderModel orderModel = instantiateModel(order);

        orderModel.add(linkTo(
                methodOn(UserController.class)
                        .getUserOrder(order.getUser().getId(), order.getId()))
                .withRel("order details"));

        orderModel.setId(order.getId());
        orderModel.setOrderDate(order.getOrderDate());
        orderModel.setOrderCost(order.getOrderCost());
        orderModel.setUser(order.getUser());
        orderModel.setCertificate(toGiftCertificateModel(order.getCertificate()));

        return orderModel;
    }

    private GiftCertificateModel toGiftCertificateModel(GiftCertificate certificate) {

        return GiftCertificateModel.builder()
                .id(certificate.getId())
                .name(certificate.getName())
                .price(certificate.getPrice())
                .duration(certificate.getDuration())
                .build()
                .add(linkTo(
                        methodOn(GiftCertificateController.class).getById(certificate.getId())).withSelfRel());
    }
}
