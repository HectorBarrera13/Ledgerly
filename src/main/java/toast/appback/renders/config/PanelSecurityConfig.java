package toast.appback.renders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el Panel de Administración
 *
 * IMPORTANTE: Esta configuración está comentada por defecto.
 * Para activarla, descomenta la anotación @Configuration y @EnableWebSecurity
 *
 * Usuario por defecto:
 *   - Username: admin
 *   - Password: admin123
 *
 * CAMBIAR ESTAS CREDENCIALES EN PRODUCCIÓN
 */
// @Configuration
// @EnableWebSecurity
public class PanelSecurityConfig {

    @Bean
    public SecurityFilterChain panelSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/panel/**", "/api/panel/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/panel/**", "/api/panel/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/panel/login")
                .defaultSuccessUrl("/panel", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/panel/logout")
                .logoutSuccessUrl("/panel/login")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/panel/**") // Para permitir POST desde el dashboard
            );

        return http.build();
    }

    @Bean
    public UserDetailsService panelUserDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

