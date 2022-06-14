package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.NodeTest;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.service.NodeTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/nodetest")
@RequiredArgsConstructor
public class NodeTestController {
    private final NodeTestService nodeTestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<NodeTest>> getAll() {
        return ResponseEntity.ok(nodeTestService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<NodeTest>> create(@RequestBody NodeTest nodeTest) {
        return ResponseEntity.ok(nodeTestService.create(nodeTest));
    }
}
