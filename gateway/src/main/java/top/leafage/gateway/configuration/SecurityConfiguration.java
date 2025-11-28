package top.leafage.gateway.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wq li
 * @since 1.4
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${app.base-uri}")
    private String appBaseUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        csrfTokenRequestAttributeHandler.setCsrfRequestAttributeName(null);

        http
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .csrf(csrf ->
                        csrf.csrfTokenRepository(cookieCsrfTokenRepository)
                                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                )
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint())
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.successHandler(new SimpleUrlAuthenticationSuccessHandler(appBaseUri)))
                .logout(logout ->
                        logout.addLogoutHandler(logoutHandler(cookieCsrfTokenRepository))
                                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                )
                .oauth2Client(Customizer.withDefaults());
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        AuthenticationEntryPoint authenticationEntryPoint =
                new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/web-client-oidc");
        MediaTypeRequestMatcher textHtmlMatcher =
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        textHtmlMatcher.setUseEquals(true);

        List<RequestMatcherEntry<@NonNull AuthenticationEntryPoint>> entryPoints = new ArrayList<>();
        RequestMatcherEntry<@NonNull AuthenticationEntryPoint> requestMatcherEntry = new RequestMatcherEntry<>(textHtmlMatcher,
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        entryPoints.add(requestMatcherEntry);

        return new DelegatingAuthenticationEntryPoint(authenticationEntryPoint, entryPoints);
    }

    private LogoutHandler logoutHandler(CsrfTokenRepository csrfTokenRepository) {
        return new CompositeLogoutHandler(
                new SecurityContextLogoutHandler(),
                new CsrfLogoutHandler(csrfTokenRepository));
    }

}
