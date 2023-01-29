package io.dankoller.github.webquizengine.controller;

import io.dankoller.github.webquizengine.request.RegistrationRequest;
import io.dankoller.github.webquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@SuppressWarnings("unused")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        return userService.register(request);
    }
}
