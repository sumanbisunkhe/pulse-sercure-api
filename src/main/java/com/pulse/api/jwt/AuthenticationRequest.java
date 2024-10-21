package com.pulse.api.jwt;

public class AuthenticationRequest {
    private String identifier; // Changed from username to identifier
    private String password;

    // Constructors, getters, and setters
    public AuthenticationRequest() {}

    public AuthenticationRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
