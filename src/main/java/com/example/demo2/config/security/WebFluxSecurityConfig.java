package com.example.demo2.config.security;

import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.SecurityContextRepository;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class WebFluxSecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;


    @Bean
    ReactiveUserDetailsService userDetailsService() {
        return (name) -> userRepository.findByUsername(name);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http.
                authorizeExchange(
                        authorizeExchangeSpec -> authorizeExchangeSpec
                                .pathMatchers("/v1/token").permitAll()
                                .pathMatchers("/v1/user").permitAll()
                                .anyExchange().authenticated()
                )
                .exceptionHandling()
                .authenticationEntryPoint((response, error) -> Mono.fromRunnable(() -> {
                    response.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }))
                .accessDeniedHandler((response, error) -> Mono.fromRunnable(() -> {
                    response.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                })).and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .requestCache().requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .build();
    }


}
