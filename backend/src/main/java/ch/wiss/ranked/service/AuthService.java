package ch.wiss.ranked.service;

import ch.wiss.ranked.dto.request.LoginRequest;
import ch.wiss.ranked.dto.request.RegisterRequest;
import ch.wiss.ranked.dto.response.AuthResponse;
import ch.wiss.ranked.entity.Role;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.exception.BadRequestException;
import ch.wiss.ranked.repository.RoleRepository;
import ch.wiss.ranked.repository.UserRepository;
import ch.wiss.ranked.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new BadRequestException("Benutzername bereits vergeben");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("E-Mail bereits registriert");

        Role role = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(Role.builder().name("STUDENT").build()));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtUtils.generateToken(auth);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(role.getName())
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtUtils.generateToken(auth);
        User user = (User) auth.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }
}
