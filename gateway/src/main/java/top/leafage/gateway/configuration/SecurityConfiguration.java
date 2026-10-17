/*
 * Copyright(c) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
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

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        // 设置attribute name 为 null
        csrfTokenRequestAttributeHandler.setCsrfRequestAttributeName(null);

        http
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                )
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint())
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.successHandler(new SimpleUrlAuthenticationSuccessHandler(appBaseUri)))
                .logout(logout -> logout
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .oauth2Client(Customizer.withDefaults());
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        AuthenticationEntryPoint authenticationEntryPoint =
                new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/web-client");

        MediaTypeRequestMatcher textHtmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        textHtmlMatcher.setUseEquals(true);

        List<RequestMatcherEntry<AuthenticationEntryPoint>> entryPoints = new ArrayList<>();
        entryPoints.add(new RequestMatcherEntry<>(textHtmlMatcher, authenticationEntryPoint));

        return new DelegatingAuthenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), entryPoints);
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }
}
