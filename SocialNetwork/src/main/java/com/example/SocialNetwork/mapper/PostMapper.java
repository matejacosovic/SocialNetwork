package com.example.SocialNetwork.mapper;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.enums.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public PostDTO toPostDto(Post post){
        return PostDTO
                .builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .text(post.getText())
                .image(post.getImage())
                .dateCreated(post.getCreatedDate())
                .status(post.getStatus())
                .build();
    }

    public Post toPost(String text, String image, User user){
        return Post
                .builder()
                .text(text)
                .image(image)
                .status(PostStatus.VISIBLE)
                .user(user)
                .build();
    }

    public void updatePost(Post post, PostDTO postDTO) {
        post.setText(postDTO.getText());
        post.setImage(postDTO.getImage());
    }
}
