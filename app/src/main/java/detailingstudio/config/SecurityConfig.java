package detailingstudio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public API endpoints
                        .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedule/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/photos/**").permitAll()
                        .requestMatchers("/api/admin/login").permitAll()
                        // Admin endpoints require authentication
                        .requestMatchers("/api/admin/**").authenticated()
                        // Static resources
                        .requestMatchers("/**").permitAll()
                )
                .httpBasic(basic -> {});

        return http.build();
    }
}
