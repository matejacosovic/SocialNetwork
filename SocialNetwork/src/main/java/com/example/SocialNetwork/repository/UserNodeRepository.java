package com.example.SocialNetwork.repository;

import com.example.SocialNetwork.domain.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserNodeRepository extends Neo4jRepository<UserNode, String> {

    @Query("MATCH (n:UserNode{id:$id}) "
            + "MATCH (n) - [r:FRIENDS_WITH] -> (friend:UserNode)"
            + "RETURN collect(friend)")
    List<UserNode> findUserFriendsById(@Param("id") String id);

    @Query("MATCH (n:UserNode{username:$username}) "
            + "MATCH (n) - [r:FRIENDS_WITH] -> (friend:UserNode)"
            + "RETURN collect(friend)")
    List<UserNode> findUserFriendsByUsername(@Param("username") String username);

    @Query("MATCH (n:UserNode{id:$id}) RETURN n")
    Optional<UserNode> findById(@Param("id") String id);

    @Query("MATCH (:UserNode {id: $firstUserId})-[r:FRIENDS_WITH]-(:UserNode {id: $secondUserId}) DELETE r")
    void deleteFriendConnection(@Param("firstUserId") String firstUserId, @Param("secondUserId") String secondUserId);

}
