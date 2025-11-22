package org.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RegistrationFilterFactory extends AbstractGatewayFilterFactory<RegistrationFilterFactory.Config> {

    private final WebClient.Builder webClientBuilder;
    private final String AUTH_SERVICE_URL = "http://auth-service:8081";
    private final String USER_SERVICE_URL = "http://user-service:8082";

    public RegistrationFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getURI().getPath().endsWith("/auth/register") || exchange.getRequest().getMethod() != HttpMethod.POST) {
                return chain.filter(exchange);
            }

            return exchange.getRequest().getBody().next()
                    .flatMap(buffer -> {
                        Map<String, Object> requestBody = readBody(buffer);
                        return webClientBuilder.build().post()
                                .uri(AUTH_SERVICE_URL + "/auth/register")
                                .bodyValue(requestBody)
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(authResponse -> {
                                    if (authResponse.getStatusCode() == HttpStatus.CREATED) {
                                        return webClientBuilder.build().post()
                                                .uri(USER_SERVICE_URL + "/api/users")
                                                .bodyValue(requestBody)
                                                .retrieve()
                                                .toEntity(Object.class)
                                                .doOnError(userError -> {

                                                    Long newUserId = 123L;

                                                    webClientBuilder.build().delete()
                                                            .uri(AUTH_SERVICE_URL + "/internal/user/" + newUserId)
                                                            .retrieve()
                                                            .toBodilessEntity()
                                                            .subscribe();

                                                })
                                                .flatMap(userResponseEntity -> {
                                                    exchange.getResponse().setStatusCode(userResponseEntity.getStatusCode());
                                                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(userResponseEntity.getBody().toString().getBytes())))
                                                            .then();
                                                });

                                    } else {
                                        return Mono.error(new Exception("Auth Service Error: " + authResponse.getStatusCode()));
                                    }
                                });
                    }).then();
        };
    }

    public static class Config {}

    private Map<String, Object> readBody(DataBuffer buffer) {
        return Map.of("username", "test", "password", "test");
    }
}