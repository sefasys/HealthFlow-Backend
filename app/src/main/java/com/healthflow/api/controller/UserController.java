package com.healthflow.api.controller;

import com.healthflow.dto.user.UpdateContactInfoRequest;
import com.healthflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{id}/contact-info")
    public ResponseEntity<Void> updateContactInfo(@PathVariable UUID id, //burada return type'a özellikle dikkat et. düzelt o kısmı.
                                                  @Valid @RequestBody UpdateContactInfoRequest request) {
        userService.updateContactInfo(id, request);
        return ResponseEntity.noContent().build();
    }
}
