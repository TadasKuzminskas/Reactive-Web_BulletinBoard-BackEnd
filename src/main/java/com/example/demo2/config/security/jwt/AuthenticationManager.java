package com.example.demo2.config.security.jwt;

import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private JWTUtil jwtUtil;
    private UserRepositoryCustom userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String userName = jwtUtil.getUsernameFromToken(token);
        System.out.println(token);

        return userRepository.findByUsername(userName)
                .flatMap(userDetails -> {
                    if(userName.equals(userDetails.getUsername()) && jwtUtil.isTokenValidated(token)) {
                        return Mono.just(authentication);
                    } else {
                        return Mono.empty();
                    }
                });
    }

}
