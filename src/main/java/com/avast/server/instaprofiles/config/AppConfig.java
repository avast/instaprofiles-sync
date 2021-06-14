package com.avast.server.instaprofiles.config;

import com.avast.server.instaprofiles.config.properties.TeamCityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Vitasek L.
 */
@Configuration
public class AppConfig {
    @Bean
    public WebClient tcWebClient(TeamCityProperties teamCityProperties) {

//        HttpClient httpClient = HttpClient.create().doOnRequest((request, connection) -> {
//            request.requestHeaders().remove("User-Agent");
//        });
        return WebClient.builder()
                .baseUrl(teamCityProperties.getUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + teamCityProperties.getToken())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build()).build();
    }
}
