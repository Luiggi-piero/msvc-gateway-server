package com.luiggi.springcloud.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.stream.Collectors;

// Es una configuracion webflux
// - configuracion para una api reactiva dentro de gateway
// - para una app que no es reactiva(spring web mvc o spring starter web, un proyecto comun y corriente) seria muy similar a shepard(archivo SecurityConfigurations)

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(authz -> {
            authz.requestMatchers("/authorized", "/logout").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")
            // .requestMatchers(HttpMethod.PUT,"/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
            // .requestMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
            // .requestMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
            .anyRequest().authenticated(); // cualquier otra ruta debe ser con autenticacion
        }).cors(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app")) // nombre de la app cliente colocada en el yml
        .oauth2Client(withDefaults())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(
            // - Obtenemos los roles del token
            // - Nos autenticamos con esos roles, en luagar de autenticarnos con los roles de scope
            // - Creamos una implementación anonima de la interfaz Converter
                jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, AbstractAuthenticationToken>(){

                    // source: es el token
                    @Override
                    public AbstractAuthenticationToken convert(Jwt source) {
                        Collection<String> roles = source.getClaimAsStringList("roles"); // Obteniendo el claim roles, coleccion de string
                        Collection<GrantedAuthority> authorities = roles.stream()  // Necesitamos una coleccion de GrantedAuthority/SimpleGrantedAuthority que son los roles
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                        return new JwtAuthenticationToken(source, authorities);
                    }
                    
                })
            ))
        .build();
    }
}
