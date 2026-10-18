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

package top.leafage.hypervisor.constants;

import java.util.List;
import java.util.Set;

public final class GlobalConstant {

    // 公共常量
    public static final String TEMP_DIR = "temp";

    public static final List<String> METADATA = List.of("id", "enabled", "created_by", "created_date", "last_modified_by", "last_modified_date");

    public static final String ID_MUST_NOT_BE_NULL = "The given id must not be null.";
    public static final String _MUST_NOT_BE_NULL = "The given %s must not be null.";
    public static final String _MUST_NOT_BE_EMPTY = "The given %s must not be empty.";

    // private construce
    private GlobalConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}
