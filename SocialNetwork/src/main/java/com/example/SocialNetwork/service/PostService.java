package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.enums.PostStatus;
import com.example.SocialNetwork.mapper.PostMapper;
import com.example.SocialNetwork.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    private final PostMapper postMapper;

    public PostDTO create(PostDTO postDTO, String usernameFromJwt) {
        User user = userService.findUser(usernameFromJwt);

        Post post = postMapper.toPost(postDTO.getText(),
                postDTO.getImage(),
                user);

        return postMapper.toPostDto(postRepository.save(post));
    }

    public PostDTO read(String id) {
        Post post = checkIfPostExists(id);
        return postMapper.toPostDto(post);
    }

    public PostDTO update(PostDTO postDTO) {
        Post post = checkIfPostExists(postDTO.getId());
        postMapper.updatePost(post, postDTO);
        postRepository.save(post);
        return postMapper.toPostDto(post);
    }

    public PostDTO delete(String id) {
        Post post = checkIfPostExists(id);
        post.setDeleted(true);
        postRepository.save(post);
        return postMapper.toPostDto(post);
    }

    public Post checkIfPostExists(String id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("There is no post with the given id: " + id);
        }
        return optionalPost.get();
    }

    public List<PostDTO> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<PostDTO> getAllByUser(String userId) {
        User user = userService.checkIfUserExists(userId);
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<PostDTO> getAllForUser(String username) {
        User user = userService.findUser(username);

        List<Post> result = new ArrayList<>(postRepository.findByUser(user));

        user.getFriends().forEach(friend -> {
            result.addAll(postRepository.findByUser(friend));
        });

        user.getFriendOf().forEach(friend -> {
            result.addAll(postRepository.findByUser(friend));
        });

        result.sort(Comparator.comparing(Post::getCreatedDate).reversed());

        return result
                .stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public PostDTO hidePost(String postId) {
        Post post = checkIfPostExists(postId);
        post.setStatus(PostStatus.HIDDEN);
        postRepository.save(post);
        return postMapper.toPostDto(post);
    }
}
