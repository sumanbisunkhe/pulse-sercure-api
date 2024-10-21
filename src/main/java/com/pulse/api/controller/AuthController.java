package com.pulse.api.controller;

import com.pulse.api.jwt.AuthenticationRequest;
import com.pulse.api.jwt.AuthenticationResponse;
import com.pulse.api.jwt.JwtUtil; // Import your JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // Add JwtUtil
    private final UserDetailsService userDetailsService; // Add UserDetailsService

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil; // Initialize JwtUtil
        this.userDetailsService = userDetailsService; // Initialize UserDetailsService
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        String identifier = request.getIdentifier();
        String password = request.getPassword();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the UserDetails from the authentication object
            UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

            // Generate the JWT token
            String jwtToken = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // Correctly obtain authorities
                    .collect(Collectors.toList()));

            // Return the success message and token
            String message = "Login successful"; // Define the success message
            return ResponseEntity.ok(new AuthenticationResponse(jwtToken, message));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
