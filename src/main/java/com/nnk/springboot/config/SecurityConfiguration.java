package com.nnk.springboot.config;

import com.nnk.springboot.config.oauth.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;


    /**
     * Spring Security needs to have a PasswordEncoder defined.
     * @return PasswordEncoder that uses the BCrypt
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/bidList/**", "/curvePoint/**", "/rating/**", "/ruleName/**", "/trade/**", "/app/secure/**").authenticated()
                        .requestMatchers("/user/list/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()

                )
//               .oauth2Login((form) ->form
//                       .userInfoEndpoint()
//                       .userService(customOAuth2UserService)
//                )

                .logout((logout) -> logout.permitAll()
                        .logoutSuccessUrl("/")

                );

        return http.build();
    }


}
