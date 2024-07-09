package com.antipin.orderapp.controller;

import com.antipin.orderapp.Views;
import com.antipin.orderapp.exception.EntityNotFoundException;
import com.antipin.orderapp.model.User;
import com.antipin.orderapp.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository repository;

    @GetMapping("/{id}")
    @JsonView(Views.UserDetails.class)
    public ResponseEntity<User> get(@PathVariable("id") Long id) {
        User user = repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @JsonView(Views.UserInfo.class)
    public ResponseEntity<Iterable<User>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        User newUser = repository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(location).body(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody @Valid User user) {
        User updatedUser = repository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
