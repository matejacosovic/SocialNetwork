package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.NodeTest;
import com.example.SocialNetwork.repository.NodeTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeTestService {
    private final NodeTestRepository nodeTestReporistory;

    public List<NodeTest> getAll(){
        return nodeTestReporistory.findAll();
    }

    public List<NodeTest> create(NodeTest nodeTest) {
        nodeTestReporistory.save(nodeTest);
        return nodeTestReporistory.findAll();
    }
}
