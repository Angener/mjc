package com.epam.esm.model.assembler;

import com.epam.esm.controller.UserController;
import com.epam.esm.entity.User;
import com.epam.esm.model.UserModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {
    public UserModelAssembler() {
        super(UserController.class, UserModel.class);
    }

    @Override
    @NonNull
    public UserModel toModel(@NonNull User user) {
        UserModel userModel = instantiateModel(user);

        userModel.add(linkTo(
                methodOn(UserController.class)
                        .findById(user.getId()))
                        .withSelfRel(),
                linkTo(
                        methodOn(UserController.class)
                                .getUserOrders(user.getId(), PageRequest.of(0, 10)))
                        .withRel("orders"));

        userModel.setId(user.getId());
        userModel.setName(user.getName());
        userModel.setRoles(user.getRoles());

        return userModel;
    }

    @Override
    @NonNull
    public CollectionModel<UserModel> toCollectionModel(
            @NonNull Iterable<? extends User> entities) {
        CollectionModel<UserModel> userModels = super.toCollectionModel(entities);
        userModels.add(linkTo(methodOn(UserController.class)
                .findAll(PageRequest.of(0, 0))).withSelfRel());
        return userModels;
    }
}
