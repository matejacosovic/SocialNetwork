package com.example.SocialNetwork.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserNode {

    @Id
    private String id;
    private String username;
    @Relationship(type = "FRIENDS_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<FriendsWith> friends;

    public void addFriend(FriendsWith friendRequest) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friendRequest);
    }
}
