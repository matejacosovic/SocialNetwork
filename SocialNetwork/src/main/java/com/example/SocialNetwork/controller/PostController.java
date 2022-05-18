package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity handleException(RuntimeException runtimeException) {
        return new ResponseEntity(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }

    @PostMapping()
    public ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO){
        return ResponseEntity.ok(postService.create(postDTO));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<PostDTO> read(@PathVariable Integer id){
        return ResponseEntity.ok(postService.read(id));
    }

    @PutMapping()
    public ResponseEntity<PostDTO> update(@RequestBody PostDTO postDTO){
        return ResponseEntity.ok(postService.update(postDTO));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<PostDTO> delete(@PathVariable Integer id){
        return ResponseEntity.ok(postService.delete(id));
    }

    @GetMapping()
    public ResponseEntity<List<PostDTO>> getAll(){
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping(value = "/get-by-user/{userId}")
    public ResponseEntity<List<PostDTO>> getAllByUser(@PathVariable Integer userId){
        return ResponseEntity.ok(postService.getAllByUser(userId));
    }

    @GetMapping(value = "/get-for-user/{userId}")
    public ResponseEntity<List<PostDTO>> getAllForUser(@PathVariable Integer userId){
        return ResponseEntity.ok(postService.getAllForUser(userId));
    }
}
