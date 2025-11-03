package com.luiggi.springcloud.app.gateway.filters.factory;


// *****************************************
// VERSION SERVLET (SPRING WEB MVC)
// *****************************************
public class SampleCookieGatewayFilterFactory {}



// *****************************************
// VERSION REACTIVO CON WEBFLUX
// - lo puedes ver sin comentarios y funcionando
// en la carpeta: msvc-gateway-server REACTIVO CON WEBFLUX
// *****************************************

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
// import org.springframework.http.ResponseCookie;
// import org.springframework.stereotype.Component;

// import reactor.core.publisher.Mono;


/*
 * a AbstractGatewayFilterFactory tenemos que pasarle una 
 * clase de configuración (ConfigurationCookie), el punto de esta clase es pasarle
 * parametros desde application.yml o application.properties
 */

// @Component
// public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.ConfigurationCookie> {


//     private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);  

//     public SampleCookieGatewayFilterFactory(){
//         super(ConfigurationCookie.class);
//     }

//     // Implementación del filtro
//     // GatewayFilter: es una interfaz funcional, es decir, tiene un solo metodo
//     // que se puede implementar con una expresion lambda
//     @Override
//     public GatewayFilter apply(ConfigurationCookie config) {
//         return new OrderedGatewayFilter((exchange, chain) -> {
            
//             logger.info("Ejecutando pre gateway filter factory: " + config.message);
            
//             return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                 Optional.ofNullable(config.value).ifPresent(cookie -> {
//                     // from usa el patron builder para construir ResponseCookie
//                     exchange.getResponse().addCookie(ResponseCookie.from(config.name, cookie).build());
//                 });
//                 logger.info("Ejecutando post gateway filter factory: " + config.message);
//             }));
//         }, 100);
//     }

//     @Override
//     public List<String> shortcutFieldOrder() {
//         return Arrays.asList("message", "name", "value");
//     }

//     @Override
//     public String name() {
//         return "EjemploCookie";
//     }

//     // Clase de configuración anidada
//     public static class ConfigurationCookie {
//         private String name;
//         private String value;
//         private String message;
//         public String getName() {
//             return name;
//         }
//         public void setName(String name) {
//             this.name = name;
//         }
//         public String getValue() {
//             return value;
//         }
//         public void setValue(String value) {
//             this.value = value;
//         }
//         public String getMessage() {
//             return message;
//         }
//         public void setMessage(String message) {
//             this.message = message;
//         }
//     }

// }
