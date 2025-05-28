/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;

/**
 * <p>AuditConfiguration class.</p>
 *
 * @author wq li
 */
@Configuration
@EnableR2dbcAuditing
public class AuditConfiguration {

    /**
     * <p>auditorProvider.</p>
     *
     * @return a {@link org.springframework.data.domain.ReactiveAuditorAware} object
     */
    @Bean
    public ReactiveAuditorAware<String> auditorProvider() {
        return () -> Mono.defer(() -> Mono.justOrEmpty(SecurityContextHolder.getContext()))
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User)
                .map(auth -> ((User) auth.getPrincipal()).getUsername());
    }
}
