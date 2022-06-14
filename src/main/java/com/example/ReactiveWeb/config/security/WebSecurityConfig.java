package com.example.ReactiveWeb.config.security;


import com.example.ReactiveWeb.repository.Custom.UserRepositoryCustom;
import com.example.ReactiveWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class WebSecurityConfig  {

    private AuthenticationManager authenticationManager;
    private SecurityContextRepository securityContextRepository;

    @Autowired
    UserRepositoryCustom userRepositoryCustom;

    @Autowired
    UserRepository userRepository;

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(
//            ServerHttpSecurity http) {
//        return http
//                .csrf().disable()
//                .authorizeExchange()
//                .pathMatchers("/v1/isPublic","/v1/user", "/login")
//                .permitAll()
//                .and()
//                .authorizeExchange()
//                .anyExchange().authenticated()
//                .and().formLogin()
//                .and().build();
//    }

    @Bean
    public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                ).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/login").permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

    @Bean
    public ReactiveUserDetailsService  userDetailsService() {
        return (username) -> userRepository.findByUsername(username);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

//@AllArgsConstructor
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//public class WebSecurityConfig {
//
//    private AuthenticationManager authenticationManager;
//    private SecurityContextRepository securityContextRepository;
//
//    @Bean
//    public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .exceptionHandling()
//                .authenticationEntryPoint((swe, e) ->
//                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
//                ).accessDeniedHandler((swe, e) ->
//                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
//                ).and()
//                .csrf().disable()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .authenticationManager(authenticationManager)
//                .securityContextRepository(securityContextRepository)
//                .authorizeExchange()
//                .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                .pathMatchers("/login").permitAll()
//                .anyExchange().authenticated()
//                .and().build();
//    }
//}