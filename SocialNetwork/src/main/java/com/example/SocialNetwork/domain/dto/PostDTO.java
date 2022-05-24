package com.example.SocialNetwork.domain.dto;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.enums.PostStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class PostDTO {
    private String id;
    private String userId;
    private String text;
    private String image;
    private Date dateCreated;
    private PostStatus status;
    public PostDTO(Post post){
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.text = post.getText();
        this.image = post.getImage();
        this.dateCreated = post.getCreatedDate();
        this.status = post.getStatus();
    }
}
