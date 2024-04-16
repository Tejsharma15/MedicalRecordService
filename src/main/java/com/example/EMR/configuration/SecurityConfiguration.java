//package com.example.EMR.configuration;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//
//import static com.example.EMR.models.Permission.*;
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfiguration {
//
//   private static final String[] WHITE_LIST_URL = {};
//   private final JwtAuthenticationFilter jwtAuthFilter;
//   private final AuthenticationProvider authenticationProvider;
//   private final LogoutHandler logoutHandler;
//
//   @Bean
//   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//       http
//               .csrf(AbstractHttpConfigurer::disable)
//               .authorizeHttpRequests(req ->
//                       req.requestMatchers(WHITE_LIST_URL)
//                               .permitAll()
//                               .requestMatchers("/emr/getPrescriptionByEmrId/{emrId}").hasAnyAuthority(PATIENT_READ.getPermission(), PRESCRIPTION_READ.getPermission())
//                               .requestMatchers("/emr/getCommentsByEmrId/{emrId}").hasAnyAuthority(PATIENT_READ.getPermission())
//                               .requestMatchers("/emr/insertNewEmr").hasAnyAuthority(PATIENT_CREATE.getPermission())
//                               .requestMatchers("/emr/updateEmrById/{emrId}").hasAnyAuthority(PATIENT_UPDATE.getPermission())
//                               .requestMatchers("/emr/deleteEmrByPatientId/{patientId}").hasAnyAuthority(PATIENT_DELETE.getPermission())
//                               .requestMatchers("/emr/getEmrByPatientId/{patientId}").hasAnyAuthority(PATIENT_READ.getPermission())
//                               .requestMatchers("/consultation/addConsultation").hasAnyAuthority(ADMIN_CREATE.getPermission(), DESK_CREATE.getPermission())
//                               .requestMatchers("/consultation/getAllConsultation").hasAnyAuthority(ADMIN_READ.getPermission())
//                               .requestMatchers("/consultation/getConsultationById/{consultationId}").hasAnyAuthority(ADMIN_READ.getPermission())
//                               .anyRequest()
//                               .authenticated()
//               )
//               .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//               .authenticationProvider(authenticationProvider)
//               .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//               .logout(logout ->
//                       logout.logoutUrl("/api/v1/auth/logout")
//                               .addLogoutHandler(logoutHandler)
//                               .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//               )
//       ;
//
//       return http.build();
//   }
//}
