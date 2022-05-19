package com.example.SocialNetwork.repository;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findByUser(User user);
}
