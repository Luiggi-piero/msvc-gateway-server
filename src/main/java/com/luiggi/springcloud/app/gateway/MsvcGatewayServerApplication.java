package com.luiggi.springcloud.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;
// import org.springframework.web.servlet.function.RouterFunction;
// import org.springframework.web.servlet.function.ServerResponse;

import reactor.core.publisher.Hooks;

// import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
// import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
// import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
// import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
// import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
// import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;

@SpringBootApplication
public class MsvcGatewayServerApplication {

	public static void main(String[] args) {
		// Habilita la propagación automática del contexto de Reactor (incluyendo traceId y spanId) 
        // antes de que se inicialice el contexto de Spring. 
        // Esto permite que los logs muestren trazabilidad automáticamente.
        Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(MsvcGatewayServerApplication.class, args);
	}

	// - Esta congifuracion se uso para mover lo referente a msvc-products del yml a este archivo
	// sobre todo lo de circuitBreaker, porque no funciona Gateway MVC con circuitBreaker(con esto tampoco funciona)
	// - El problema es que no reconoce msvc-products a pesar de que sea una ruta libre
	// - Lo recomendable es usar Gateway Reactivo
	/* @Bean
	RouterFunction<ServerResponse> routerConfig(){
		return route("msvc-products") // id del msvc, es el nombre del proyecto que se encuentra en el properties de msvc-products
			.route(
				path("/api/products/**"), // patron para acceder al msvc de productos
				http()
			)
			.filter(lb("msvc-products")) // balanceo de carga con multiples instancias
			.filter(circuitBreaker(config -> config // filtro circuit breaker para manejar errores
				.setId("products") // nombre de la instancia del circuit breaker
				.setStatusCodes("500") // cuando se abre el circuito? cuando ocurre un error 500
				.setFallbackPath("forward:/api/items/5"))) // camino alternativo si falla
			.before(stripPrefix(2)) // la ruta tiene 2 niveles/prefijos son: api e items, es otro tipo de filtro
			.build();
	} */

}
