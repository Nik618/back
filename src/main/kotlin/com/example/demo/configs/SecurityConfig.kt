package com.example.demo.configs

import com.example.demo.components.JwtFilterComponent
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
        private val jwtFilterComponent: JwtFilterComponent
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests() { authz ->
                    authz
                            .antMatchers("/login", "/token", "/sign").permitAll()
                            .anyRequest().authenticated()
                            .and()
                            .addFilterAfter(jwtFilterComponent, UsernamePasswordAuthenticationFilter::class.java)
                }

    }
//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        return http
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests() { authz ->
//                    authz
//                            .antMatchers("/api/auth/login", "/api/auth/token").permitAll()
//                            .anyRequest().authenticated()
//                            .and()
//                            .addFilterAfter(jwtFilterComponent, UsernamePasswordAuthenticationFilter::class.java)
//                }.build()
//    }


}