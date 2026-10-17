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

package top.leafage.gateway.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * secrity exception advice
 *
 * @author wq li
 */
@ControllerAdvice
public class SecurityExceptionAdvice {

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<String> handleOAuth2Exception(OAuth2AuthorizationException ex, HttpServletRequest request) {
        if ("invalid_grant".equals(ex.getError().getErrorCode())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // 强制清除网关本地 Session
            }
            // 返回 401 给前端
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authorization expired, local session cleared.");
        }

        // 其他 OAuth2 错误保持原样或返回 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
