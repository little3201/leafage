/*
 * Copyright (c) 2024-2025.  little3201.
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

public final class FieldConstant {

    // 公共常量
    public static final String FORM_INPUT = "input";
    public static final String FORM_INPUT_NUMBER = "input-number";
    public static final String FORM_TEXTAREA = "textarea";
    public static final String FORM_SELECT = "select";
    public static final String FORM_SWITCH = "switch";
    public static final String FORM_DATE_PICKER = "date-picker";
    public static final String FORM_TIME_PICKER = "time-picker";

    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_DATE = "Date";

    public static final String JAVA_SHORT = "Short";
    public static final String JAVA_INTEGER = "Integer";
    public static final String JAVA_LONG = "Long";
    public static final String JAVA_FLOAT = "Float";
    public static final String JAVA_DOUBLE = "Double";
    public static final String JAVA_BIGDECIMAL = "BigDecimal";
    public static final String JAVA_STRING = "String";
    public static final String JAVA_BOOLEAN = "Boolean";
    public static final String JAVA_INETADDRESS = "InetAddress";
    public static final String JAVA_LOCALDATE = "LocalDate";
    public static final String JAVA_LOCALTIME = "LocalTime";
    public static final String JAVA_LOCALDATETIME = "LocalDateTime";

    // private construce
    private FieldConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}
