package com.example.SocialNetwork.domain.dto;

import com.example.SocialNetwork.domain.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private String id;
    private String userId;
    @NotBlank(message = "Text is mandatory")
    private String text;
    private String image;
    private Date dateCreated;
    private PostStatus status;
}
