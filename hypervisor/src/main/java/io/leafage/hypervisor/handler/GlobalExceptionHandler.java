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

package io.leafage.hypervisor.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.EntityResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * rest controller advice
 *
 * @author wq li
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * <p>handleMethodArgumentNotValidException.</p>
     *
     * @param e a {@link org.springframework.web.bind.MethodArgumentNotValidException} object
     * @return a {@link reactor.core.publisher.Mono} object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Mono<EntityResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : allErrors) {
            errorMsg.append(error.getDefaultMessage()).append("; ");
        }

        return EntityResponse.fromObject(errorMsg.toString()).status(HttpStatus.NOT_FOUND).build();
    }
}
