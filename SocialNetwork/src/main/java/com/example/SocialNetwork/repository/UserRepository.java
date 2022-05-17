package com.example.SocialNetwork.repository;

import com.example.SocialNetwork.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
