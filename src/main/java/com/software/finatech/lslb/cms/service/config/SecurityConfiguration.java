package com.software.finatech.lslb.cms.userservice.config;

import com.google.common.collect.ImmutableList;
import com.software.finatech.jjwt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
//@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationProvider authenticationProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        //@TODO add this config to yml
        List<String> pathsToSkip = Arrays.asList("/actuator/**",
        "/api/v1/referencedata/**",
        "/api/v1/authInfo/complete",
        "/api/v1/authInfo/confirmPasswordReset",
        "/api/v1/authInfo/resetpasswordvalidation",
        "/api/v1/authInfo/resetPassword",
        "/api/v1/authInfo/resendToken",
        "/api/v1/authInfo/login",
        "/api/v1/authInfo/confirm",
        "/api/v1/authInfo/new-gaming-operator",
        "/api/v1/gameType/allgametypes",
        "/v2/api-docs",
         "/configuration/ui", 
         "/swagger-resources/**", 
         "/configuration/security", 
         "/docApi/**",
          "/webjars/**");
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, "/**");

        JwtAuthenticationTokenFilter authenticationTokenFilter = new JwtAuthenticationTokenFilter(matcher);
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        return authenticationTokenFilter;
    }
    /*@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v1/healthInstitution/new","/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }*/

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors().and()
                //.cors().and()
                //.addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class)
                .csrf().disable()
                //.authorizeRequests()
                //.antMatchers("/api/v1/healthInstitution/new","/api/v1/healthInstitution/confirm","/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**")
                //.permitAll()
                //.and()
                //.authorizeRequests()//.anyRequest().authenticated()
                //.antMatchers("/v1/**").authenticated() // Protected API End-points
                //.antMatchers("/**").authenticated()
                //.and()
                // Call our errorHandler if authentication/authorisation fails
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //.and()

        //Validate IPs for pos
        /*httpSecurity
                .authorizeRequests()
                .antMatchers("/api/v1/pos/invoicing/**").access("hasIpAddress('10.103.0.30') or hasIpAddress('52.19.92.242')  or hasIpAddress('127.0.0.1')")
                .antMatchers("/api/v1/hnb/invoicing/**").access("hasIpAddress('192.168.8.101') or hasIpAddress('10.103.0.30')  or hasIpAddress('192.168.1.0/24')");
                */
        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity.headers().cacheControl();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        //@TODO make this configurable
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "x-xsrf-token", "TenantId"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                        .getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                            && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}
