package com.example.employeedirectoryproject.config;

import com.example.employeedirectoryproject.util.TbConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/change_password/**").permitAll()
                        .requestMatchers("/forgot_password/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .requestMatchers("/add_new_employee/**").hasAnyRole("ADMIN")
                        .requestMatchers("/edit_profile/**").hasAnyRole("EMPLOYEE","ADMIN")
                        .requestMatchers("/list_employees/**").hasAnyRole("ADMIN")
                        .requestMatchers("/add_new_project/**").hasAnyRole("ADMIN")
                        .requestMatchers("/add_new_department/**").hasAnyRole("ADMIN")
                        .requestMatchers("/list_departments/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .exceptionHandling().accessDeniedPage("/access_denied");
        return http.build();
    }

}
