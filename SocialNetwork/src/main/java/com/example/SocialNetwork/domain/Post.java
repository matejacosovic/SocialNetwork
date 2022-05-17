package com.example.SocialNetwork.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Where(clause = "deleted = false")
@Data
@NoArgsConstructor
public class Post extends BaseEntity {
    @Column(columnDefinition="text", nullable = true)
    private String text;

    @Column(columnDefinition="text", nullable = true)
    private String image;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
