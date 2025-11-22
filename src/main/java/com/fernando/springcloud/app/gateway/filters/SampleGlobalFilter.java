package com.fernando.springcloud.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

     private Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

     @Override
     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
          logger.info("ejecutando el filtro antes del request PRE");

          // Crear un request nuevo con el header
          var request = exchange.getRequest().mutate()
                    .headers(h -> h.add("token", "abcdfghf")).build();

          // Crear un exchange nuevo con ese request
          var mutatedExchange = exchange.mutate().request(request).build();

          return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
               logger.info("ejecutando el filtro POST response");

               String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
               if(token != null) {
                    logger.info("token: " + token);
                    mutatedExchange.getResponse().getHeaders().add("token", token);
               }
               

               Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token")).ifPresent(value ->{
                    logger.info("token: " + value);

                    mutatedExchange.getResponse().getHeaders().add("token", value);
               });
                         

               mutatedExchange.getResponse().getCookies().add("color",
                         ResponseCookie.from("color", "red").build());
              // mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
               /*
                * exchange.getResponse().getCookies().add("color", ResponseCookie.from("color",
                * "red").build());
                * exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                */

          }));

     }

     @Override
     public int getOrder() {
          return 100;
     }

}
