package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    public PostDTO create(PostDTO postDTO) {
        User user = userService.checkIfUserExists(postDTO.getUserId());

        Post post = new Post(postDTO.getText(),
                postDTO.getImage(),
                LocalDateTime.now(),
                user
        );
        postRepository.save(post);

        return new PostDTO(post);
    }

    public PostDTO read(String id) {
        Post post = checkIfPostExists(id);
        return new PostDTO(post);
    }

    public PostDTO update(PostDTO postDTO) {
        Post post = checkIfPostExists(postDTO.getId());
        post.setText(postDTO.getText());
        post.setImage(postDTO.getImage());
        postRepository.save(post);
        return new PostDTO(post);
    }

    public PostDTO delete(String id) {
        Post post = checkIfPostExists(id);
        post.setDeleted(true);
        postRepository.save(post);
        return new PostDTO(post);
    }

    public Post checkIfPostExists(String id){
        Optional<Post> optionalPost = postRepository.findById(id);
        if(optionalPost.isEmpty()){
            throw new RuntimeException("There is no post with the given id: " + id);
        }
        return optionalPost.get();
    }

    public List<PostDTO> getAll() {
        return postRepository.findAll()
                .stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    public List<PostDTO> getAllByUser(String userId) {
        User user = userService.checkIfUserExists(userId);
        return postRepository.findByUser(user)
                .stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    public List<PostDTO> getAllForUser(String userId) {
        User user = userService.checkIfUserExists(userId);

        List<Post> result = new ArrayList<>(postRepository.findByUser(user));

        user.getFriends().forEach(friend -> {
            result.addAll(postRepository.findByUser(friend));
        });

        user.getFriendOf().forEach(friend -> {
            result.addAll(postRepository.findByUser(friend));
        });

        result.sort(Comparator.comparing(Post::getDateCreated).reversed());

        return result.stream().map(PostDTO::new).collect(Collectors.toList());
    }
}
