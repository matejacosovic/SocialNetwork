package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO){
        return ResponseEntity.ok(postService.create(postDTO));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<PostDTO> read(@PathVariable String id){
        return ResponseEntity.ok(postService.read(id));
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<PostDTO> update(@RequestBody PostDTO postDTO){
        return ResponseEntity.ok(postService.update(postDTO));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<PostDTO> delete(@PathVariable String id){
        return ResponseEntity.ok(postService.delete(id));
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<PostDTO>> getAll(){
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping(value = "/wall")
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<List<PostDTO>> getAllByUser(@RequestParam(name = "userId") String userId){
        return ResponseEntity.ok(postService.getAllByUser(userId));
    }

    @GetMapping(value = "/feed")
    @PreAuthorize("hasAnyAuthority('ROLE_APPUSER', 'ROLE_ADMIN')")
    public ResponseEntity<List<PostDTO>> getAllForUser(@RequestParam(name = "userId") String userId){
        return ResponseEntity.ok(postService.getAllForUser(userId));
    }

    @PutMapping(value = "/hide")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<PostDTO> hidePost(@RequestParam(name = "postId") String postId){
        return ResponseEntity.ok(postService.hidePost(postId));
    }
}
