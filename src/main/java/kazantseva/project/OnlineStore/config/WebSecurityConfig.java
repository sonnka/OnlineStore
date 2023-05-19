package kazantseva.project.OnlineStore.config;

import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.service.CustomerService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/v1/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/v1/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/v1/logout");

        http.headers().frameOptions().disable();
        http.csrf().disable().authorizeHttpRequests((request) -> request
                .requestMatchers("/").permitAll()
                .requestMatchers("/v1/**").permitAll()
                .requestMatchers("/home").permitAll()
                .requestMatchers("/products").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/customers/**").authenticated()
                .anyRequest().authenticated()).httpBasic();

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
