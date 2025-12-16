package com.luiggi.springcloud.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.stream.Collectors;

// Es una configuracion webflux
// - configuracion para una api reactiva dentro de gateway
// - para una app que no es reactiva(spring web mvc o spring starter web, un proyecto comun y corriente) seria muy similar a shepard(archivo SecurityConfigurations)

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception{
        return http.authorizeExchange(authz -> {
            authz.pathMatchers("/authorized", "/logout").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasAnyRole("USER", "ADMIN")
            .pathMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")
            // .pathMatchers(HttpMethod.PUT,"/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
            // .pathMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
            // .pathMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
            .anyExchange().authenticated(); // cualquier otra ruta debe ser con autenticacion
        }).cors(csrf -> csrf.disable())
        .oauth2Login(withDefaults())
        .oauth2Client(withDefaults())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(
            // - Obtenemos los roles del token
            // - Nos autenticamos con esos roles, en luagar de autenticarnos con los roles de scope
            // - Creamos una implementación anonima de la interfaz Converter
                jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, Mono<AbstractAuthenticationToken>>(){

                    // source: es el token
                    @Override
                    public Mono<AbstractAuthenticationToken> convert(Jwt source) {
                        Collection<String> roles = source.getClaimAsStringList("roles"); // Obteniendo el claim roles, coleccion de string
                        Collection<GrantedAuthority> authorities = roles.stream()  // Necesitamos una coleccion de GrantedAuthority/SimpleGrantedAuthority que son los roles
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                        return Mono.just(new JwtAuthenticationToken(source, authorities));
                    }
                    
                })
            ))
        .build();
    }
}
