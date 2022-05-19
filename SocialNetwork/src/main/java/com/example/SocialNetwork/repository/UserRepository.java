package com.example.SocialNetwork.repository;

import com.example.SocialNetwork.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE %?1%"
            + " OR LOWER(u.surname) LIKE %?1%"
            + " OR LOWER(u.username) LIKE %?1%"
            + " OR LOWER(CONCAT(u.name, ' ', u.surname)) LIKE %?1%")
    List<User> search(String keyword);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
