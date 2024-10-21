package com.pulse.api.jwt;

import lombok.Getter;

@Getter
public class AuthenticationResponse {
    private final String jwt;
    private final String message;

    public AuthenticationResponse(String jwt, String message) {
        this.jwt = jwt;
        this.message = message;
    }

}
