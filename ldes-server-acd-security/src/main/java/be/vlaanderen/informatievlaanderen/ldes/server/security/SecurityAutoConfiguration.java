package be.vlaanderen.informatievlaanderen.ldes.server.security;



import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static be.cumuli.security.SecurityOrder.API;
import static be.cumuli.security.SecurityOrder.BROWSER;

@Slf4j
@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class SecurityAutoConfiguration {

    @Bean
    @org.springframework.core.annotation.Order(API)
    SecurityFilterChain apiSecurityFilterchain(HttpSecurity http,
                                               JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(
                        auth -> {
                            // todo rol zetten
                            // Alle post request
                            //auth.requestMatchers(HttpMethod.POST).authenticated();
                            // alles op het admin endpoint
                            //auth.requestMatchers("/admin/**").authenticated();
                            // any other page
                            auth.anyRequest().permitAll();
                        })
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer(
                        c -> c.jwt(cj -> cj.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

}

