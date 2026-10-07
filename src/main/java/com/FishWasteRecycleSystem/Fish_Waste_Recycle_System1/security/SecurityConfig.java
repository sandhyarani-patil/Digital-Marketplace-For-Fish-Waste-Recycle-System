//package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                .authorizeHttpRequests(auth -> auth
//
//                        // =========================
//                        // LOGIN / REGISTER
//                        // =========================
//
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/user").permitAll()
//
//
//                        // =========================
//                        // WASTE LISTING
//                        // =========================
//
//                        .requestMatchers(HttpMethod.POST, "/api/wastelistings")
//                        .hasRole("SELLER")
//
//                        .requestMatchers(HttpMethod.PUT, "/api/wastelistings/**")
//                        .hasRole("SELLER")
//
//                        .requestMatchers(HttpMethod.PATCH, "/api/wastelistings/**")
//                        .hasRole("SELLER")
//
//                        .requestMatchers(HttpMethod.DELETE, "/api/wastelistings/**")
//                        .hasRole("SELLER")
//
//                        .requestMatchers(HttpMethod.GET, "/api/wastelistings/**")
//                        .authenticated()
//
//
//                        // =========================
//                        // REQUIREMENT
//                        // =========================
//
//                        .requestMatchers(HttpMethod.POST, "/api/requirements")
//                        .hasRole("COMPANY")
//
//                        .requestMatchers(HttpMethod.PUT, "/api/requirements/**")
//                        .hasRole("COMPANY")
//
//                        .requestMatchers(HttpMethod.PATCH, "/api/requirements/**")
//                        .hasRole("COMPANY")
//
//                        .requestMatchers(HttpMethod.DELETE, "/api/requirements/**")
//                        .hasRole("COMPANY")
//
//                        .requestMatchers(HttpMethod.GET, "/api/requirements/**")
//                        .authenticated()
//
//
//                        // =========================
//                        // ORDERS
//                        // =========================
//                        .requestMatchers(
//                                HttpMethod.PATCH,
//                                "/api/orders/*/accept"
//                        )
//                        .hasRole("SELLER")
//
//                        // Seller cancels order
//                        .requestMatchers(
//                                HttpMethod.PATCH,
//                                "/api/orders/*/cancel"
//                        )
//                        .hasRole("SELLER")
//
//                        // View orders
//                        .requestMatchers(HttpMethod.GET, "/api/orders/**")
//                        .hasAnyRole("SELLER", "COMPANY", "ADMIN")
//
//                        // Create order
//                        .requestMatchers(HttpMethod.POST, "/api/orders")
//                        .hasRole("COMPANY")
//
//                        // Update order
//                        .requestMatchers(HttpMethod.PUT, "/api/orders/**")
//                        .hasAnyRole("COMPANY", "ADMIN")
//
//                        // Partial update / Cancel
//                        .requestMatchers(HttpMethod.PATCH, "/api/orders/**")
//                        .hasAnyRole("COMPANY", "ADMIN")
//
//                        // Delete order
//                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**")
//                        .hasRole("ADMIN")
//
//
//                        // =========================
//                        // COMPANY
//                        // =========================
//
//                        .requestMatchers("/api/company/**")
//                        .hasAnyRole("COMPANY", "ADMIN")
//
//
//                        // =========================
//                        // EVERYTHING ELSE
//                        // =========================
//
//                        .anyRequest().authenticated()
//                )
//
//                .addFilterBefore(
//                        jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class
//                );
//
//        return http.build();
//    }
//}


package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // LOGIN / REGISTER
                        // =========================

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/user").permitAll()


                        // =========================
                        // WASTE LISTING
                        // =========================

                        .requestMatchers(HttpMethod.POST, "/api/wastelistings")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.PUT, "/api/wastelistings/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.PATCH, "/api/wastelistings/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.DELETE, "/api/wastelistings/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.GET, "/api/wastelistings/**")
                        .authenticated()


                        // =========================
                        // REQUIREMENT
                        // =========================

                        .requestMatchers(HttpMethod.POST, "/api/requirements")
                        .hasRole("COMPANY")

                        .requestMatchers(HttpMethod.PUT, "/api/requirements/**")
                        .hasRole("COMPANY")

                        .requestMatchers(HttpMethod.PATCH, "/api/requirements/**")
                        .hasRole("COMPANY")

                        .requestMatchers(HttpMethod.DELETE, "/api/requirements/**")
                        .hasRole("COMPANY")

                        .requestMatchers(HttpMethod.GET, "/api/requirements/**")
                        .authenticated()


                        // =========================
                        // ORDERS
                        // =========================
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/orders/*/accept"
                        )
                        .hasRole("SELLER")

                        // Seller cancels order
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/orders/*/cancel"
                        )
                        .hasRole("SELLER")

                        // View orders
                        .requestMatchers(HttpMethod.GET, "/api/orders/**")
                        .hasAnyRole("SELLER", "COMPANY", "ADMIN")

                        // Create order
                        .requestMatchers(HttpMethod.POST, "/api/orders")
                        .hasRole("COMPANY")

                        // Update order
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**")
                        .hasAnyRole("COMPANY", "ADMIN")

                        // Partial update / Cancel
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/**")
                        .hasAnyRole("COMPANY", "ADMIN")

                        // Delete order
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**")
                        .hasRole("ADMIN")


                        // =========================
                        // COMPANY
                        // =========================

                        .requestMatchers("/api/company/**")
                        .hasAnyRole("COMPANY", "ADMIN")



                                // =========================
// COMPANY DELETE RESTRICTION (फक्त ADMIN साठी)
// =========================
                                .requestMatchers(HttpMethod.DELETE, "/api/company/**")
                                .hasRole("ADMIN")

// =========================
// SELLER DELETE RESTRICTION (फक्त ADMIN साठी)
// =========================
                                .requestMatchers(HttpMethod.DELETE, "/api/seller/**")
                                .hasRole("ADMIN")

// =========================
// USER DELETE RESTRICTION (फक्त ADMIN साठी)
// =========================
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**")
                                .hasRole("ADMIN")


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}