package com.epam.esm.service.role;

import com.epam.esm.entity.Role;
import com.epam.esm.repository.RoleRepository;
import com.epam.esm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;

    @Autowired
    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Role save(Role role) {
        return repository.save(role);
    }
}
