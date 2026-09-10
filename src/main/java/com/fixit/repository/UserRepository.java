package com.fixit.repository;

import com.fixit.entity.User;
import com.fixit.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Boolean existsByUsername(String username);

    Boolean existsByUsernameIgnoreCase(String username);

    List<User> findByRole(Role role);

    List<User> findByRoleOrderByNameAsc(Role role);
}
