package com.catalogo.backend.config;

import com.catalogo.backend.entity.Usuario;
import com.catalogo.backend.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByNombreUsuarioAndActivoTrue(username)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    private org.springframework.security.core.userdetails.UserDetails toUserDetails(Usuario usuario) {
        return User.withUsername(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .authorities(usuario.getRoles().stream()
                        .map(link -> new SimpleGrantedAuthority("ROLE_" + link.getRol().getNombre().toUpperCase()))
                        .toList())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**",
                                "/api/categorias/**", "/api/catalogos/**", "/api/paginas/**")
                        .permitAll()
                        .requestMatchers("/api/auth/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().denyAll());
        return http.build();
    }
}
