package kazantseva.project.OnlineStore.config;

import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.service.CustomerService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((request) -> request
                .requestMatchers("/").permitAll()
                .requestMatchers("/home").permitAll()
                .requestMatchers("/products").permitAll()
                .requestMatchers("/products_html").permitAll()
                .requestMatchers("/confirm-email").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/customers/**").authenticated()
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/profile/**").authenticated()
                .anyRequest().authenticated());

        http.httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c
                        .frameOptions()
                        .disable());

        http.formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomerRepository customerRepository, CustomerService mapper) {
        return email -> customerRepository.findByEmailIgnoreCase(email)
                .map(mapper::toUserDetails).orElseThrow(() -> new UsernameNotFoundException(email + " not found"));
    }
}
