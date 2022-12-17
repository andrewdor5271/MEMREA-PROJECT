package org.mirea.pm.notes_backend.controllers.auth;

import org.mirea.pm.notes_backend.controllers.auth.payload.*;
import org.mirea.pm.notes_backend.db.*;
import org.mirea.pm.notes_backend.security.JwtUtils;
import org.mirea.pm.notes_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {

        if(!userRepository.existsByName(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new InvalidSignInResponse());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new SignInSuccessfulResponse(jwt, userDetails.getId()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByName(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new InvalidSignUpResponse());
        }

        User user = new User(request.getUsername(),
                encoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: User role is not found."));

        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        return ResponseEntity.ok(new OkAuthResponse());
    }

    @PostMapping("/extend")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> extendToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if(authorization.startsWith("Bearer ")) {
            authorization = authorization.split(" ")[1];
        }
        String username = jwtUtils.getUserNameFromJwtToken(authorization);
        String newJwt = jwtUtils.generateJwtToken(username);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new SignInSuccessfulResponse(newJwt, userDetails.getId()));
    }
}
