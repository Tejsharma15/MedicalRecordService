// package com.example.EMR.configuration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
// @Configuration
// @Profile("dev")
// public class DevelopmentSecurityConfig{
//
//     @Bean
//     public WebSecurityCustomizer webSecurityCustomizer() {
//         return (web) -> web.ignoring()
//                 .requestMatchers(new AntPathRequestMatcher("/**"));
//     }
// }
