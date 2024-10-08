package com.fiap.security_system.auth;


import com.fiap.security_system.service.EmployeeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EmployeeService employeeService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, EmployeeService employeeService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.employeeService = employeeService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/incidents/**").hasAnyRole("ADMIN", "POLICE_OFFICER")
                                .requestMatchers(HttpMethod.PATCH, "/api/incidents/**").hasAnyRole("ADMIN", "POLICE_OFFICER")
                                .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/incidents").hasAnyRole("ADMIN", "POLICE_OFFICER")
                                .requestMatchers(HttpMethod.DELETE, "/api/employees").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/incidents").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
