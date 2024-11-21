package com.gobidder.auth.controller;

import com.gobidder.auth.model.User;
import com.gobidder.auth.dtos.LoginDto;
import com.gobidder.auth.dtos.RegisterDto;
import com.gobidder.auth.response.LoginResponse;
import com.gobidder.auth.service.AuthService;
import com.gobidder.auth.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;

    private final AuthService authenticationService;

    public AuthController(JwtService jwtService, AuthService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterDto registerDto) {
        User registeredUser = authenticationService.signup(registerDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginDto) {
        User authenticatedUser = authenticationService.authenticate(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    @RequestMapping(
        value = "",
        method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
        }
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void proxyAuthenticate() {
        logger.info("proxyAuthenticate");
    }
}
