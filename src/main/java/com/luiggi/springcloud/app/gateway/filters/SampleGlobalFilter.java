package com.luiggi.springcloud.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
// import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    /* 
     * exchange: maneja el request y la respuesta
     * chain: la cadena de filtros
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // ********* INICIO PRE
        logger.info("Ejecutando el filtro antes del request PRE");

        // header(...) se usa para agregar o sobrescribir una cabecera directamente.
        // headers(h -> h.add(...)) intenta acceder al objeto original, que en este caso es de solo lectura, y ahí falla

         /*
         * Con esto, no modificas directamente los headers existentes (que son inmutables), 
         * sino que creas una nueva request con las cabeceras que tú quieres.
         */

        // agregando un token
        //exchange.getRequest().mutate().headers(h -> h.add("token", "abcdefg"));
        ServerHttpRequest mutatedRequest = exchange.getRequest()
        .mutate()
        .header("token", "abcdefg")
        .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
        .request(mutatedRequest)
        .build();
        // ********* FIN PRE
        
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            // ********* INICIO POST
            logger.info("Ejecutando filtro POST response");

            // Forma 1 para leer el token
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if(token != null){
                // Mostrando el token
                logger.info("token1 forma normal " + token);
                // Agregar el token al response
                mutatedExchange.getResponse().getHeaders().add("token1", token);
            }

            // Forma 2 para leer el token, mas elegante
            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token")).ifPresent(value -> {
                logger.info("token2 forma reactiva " + value);
                // Agregar el token al response
                mutatedExchange.getResponse().getHeaders().add("token2", value);
            });

            // agregamos una cookie al response
            mutatedExchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            // modificamos el content type al response
            // mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

            // ********* FIN POST
        }));

        /* 
         * Con el then ejecutamos algo despues de que se emitiera la respuesta
         */
    }

    /*
     * Los filtros tienen un orden
     * - El primero en entrar es el ultimo en salir, como una pila
     * Ejemplo:
     * Si tenemos 2 filtros, uno con el orden 100 y el otro con 101
     * el filtro con orden 100 entrará primero en PRE, es decir, se ejecutará primero su PRE del 100 
     * y luego el PRE del 101
     * 
     * sobre el POST primero se ejecutará el POST del 101 y al final del 100
     * el primero en entrar es el último en salir
     */

    @Override
    public int getOrder() {
        // orden 100, no olvidar implementar Ordered
        return 100;
    }

}
