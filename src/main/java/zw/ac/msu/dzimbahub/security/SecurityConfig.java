package zw.ac.msu.dzimbahub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
 private final JwtAuthenticationFilter jwtFilter; public SecurityConfig(JwtAuthenticationFilter jwtFilter){this.jwtFilter=jwtFilter;}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers("/api/auth/login","/api/auth/signup","/api/health","/error").permitAll()
    .requestMatchers("/api/auth/me").authenticated()
    .requestMatchers(HttpMethod.GET,"/api/properties/**","/api/recommendations/**").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/recommendations").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/viewings/**","/api/messages/**","/api/reviews/**").authenticated()
    .anyRequest().permitAll()).addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();
 }
}
