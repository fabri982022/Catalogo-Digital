package com.catalogo.backend.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/session")
    public Map<String, String> session(Principal principal) {
        return Map.of("nombreUsuario", principal.getName());
    }
}
