package be.vlaanderen.informatievlaanderen.ldes.server.security;


import be.cumuli.security.WebServerSecurityCustomizer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

@Slf4j
@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class SecurityAutoConfiguration {
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring()
                    // Spring Security should completely ignore following resources
                    .requestMatchers(
                            "/index.html",
                            "/i18n/**",
                            "/images/**",
                            "/node_modules/**",
                            "/robots.txt",
                            "/*.js");
        };
    }


    @Bean
    WebServerSecurityCustomizer webServerSecurityCustomizer() {
        log.info("Securing admin and actuator endpoints.");
        return this::webServerSecurity;
    }

    @SneakyThrows
    private HttpSecurity webServerSecurity(HttpSecurity httpSecurity) {
        return httpSecurity
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(
                        auth -> {
                            // landing page
                            // todo rol zetten
                            auth.requestMatchers("/admin/**").authenticated();
                            // any other page
                            auth.anyRequest().permitAll();
                        });
    }
}

