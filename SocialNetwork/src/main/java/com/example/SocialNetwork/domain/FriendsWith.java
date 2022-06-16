package com.example.SocialNetwork.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@RelationshipProperties
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsWith {

    @RelationshipId
    private Long id;

    private LocalDateTime createdAt;

    @TargetNode
    private UserNode user;

}
