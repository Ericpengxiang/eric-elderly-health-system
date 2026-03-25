package com.health.module.user.repository;

import com.health.common.enums.RoleEnum;
import com.health.module.user.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    boolean existsByUsername(String username);

    List<SysUser> findByRole(RoleEnum role);

    long countByRole(RoleEnum role);

    long countByCreateTimeAfter(LocalDateTime from);
}
