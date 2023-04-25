package com.nnk.springboot.repositories;

import com.nnk.springboot.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Role, Integer> {
}
