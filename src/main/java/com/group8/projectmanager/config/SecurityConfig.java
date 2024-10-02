package com.group8.projectmanager.config;

import com.group8.projectmanager.converter.JwtToAuthernticationConverter;
import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtToAuthernticationConverter jwtConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(CsrfConfigurer::disable)

            .authorizeHttpRequests(authorize -> {
                authorize.anyRequest().permitAll();
            })

            .oauth2ResourceServer(oauth2 -> {
                oauth2.jwt(jwt -> {
                    jwt.jwtAuthenticationConverter(jwtConverter);
                });
            })

            .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        var authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(this::loadByUserName);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    private User loadByUserName(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> {
                return new UsernameNotFoundException("User not found");
            });
    }
}
