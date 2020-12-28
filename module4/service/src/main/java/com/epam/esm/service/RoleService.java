package com.epam.esm.service;

import com.epam.esm.entity.Role;

public interface RoleService {
    Role findByName(String name);

    Role save(Role role);
}
