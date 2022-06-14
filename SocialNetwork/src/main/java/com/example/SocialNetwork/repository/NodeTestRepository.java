package com.example.SocialNetwork.repository;

import com.example.SocialNetwork.domain.NodeTest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface NodeTestRepository extends Neo4jRepository<NodeTest, Long> {
}
