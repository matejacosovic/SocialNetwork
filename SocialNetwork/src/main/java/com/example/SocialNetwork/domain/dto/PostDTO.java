package com.example.SocialNetwork.domain.dto;

import com.example.SocialNetwork.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDTO {
    private String id;
    private String userId;
    private String text;
    private String image;
    private LocalDateTime dateCreated;

    public PostDTO(Post post){
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.text = post.getText();
        this.image = post.getImage();
        this.dateCreated = post.getDateCreated();
    }
}
