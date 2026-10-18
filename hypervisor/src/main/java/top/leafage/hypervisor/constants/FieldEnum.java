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


import static top.leafage.hypervisor.constants.FieldEnum.Type.*;

/**
 * 数据库类型映射到Java类型、组件名和TypeScript类型
 *
 * @author wq li
 */
public enum FieldEnum {

    INT2("int2", JAVA_SHORT, FORM_INPUT_NUMBER, TYPE_NUMBER),
    INT4("int4", JAVA_INTEGER, FORM_INPUT_NUMBER, TYPE_NUMBER),
    INT8("int8", JAVA_LONG, FORM_SELECT, TYPE_NUMBER),
    FLOAT4("float4", JAVA_FLOAT, FORM_INPUT_NUMBER, TYPE_NUMBER),
    FLOAT8("float8", JAVA_DOUBLE, FORM_INPUT_NUMBER, TYPE_NUMBER),
    NUMERIC("numeric", JAVA_BIGDECIMAL, FORM_INPUT_NUMBER, TYPE_NUMBER),
    BOOL("bool", JAVA_BOOLEAN, FORM_SWITCH, TYPE_BOOLEAN),
    VARCHAR("varchar", JAVA_STRING, FORM_INPUT, TYPE_STRING),
    INET("inet", JAVA_INETADDRESS, FORM_INPUT, TYPE_STRING),
    TEXT("text", JAVA_STRING, FORM_TEXTAREA, TYPE_STRING),
    DATE("date", JAVA_LOCALDATE, FORM_DATE_PICKER, TYPE_DATE),
    TIME("time", JAVA_LOCALTIME, FORM_TIME_PICKER, TYPE_DATE),
    TIMESTAMP("timestamp", JAVA_LOCALDATETIME, FORM_DATE_PICKER, TYPE_DATE),
    TIMESTAMPTZ("timestamptz", JAVA_LOCALDATETIME, FORM_DATE_PICKER, TYPE_DATE),
    JSON("json", JAVA_STRING, FORM_INPUT, TYPE_STRING);

    private final String dbType;
    private final String javaType;
    private final String formType;
    private final String tsType;

    FieldEnum(String dbType, String javaType, String formType, String tsType) {
        this.dbType = dbType;
        this.javaType = javaType;
        this.formType = formType;
        this.tsType = tsType;
    }

    public static FieldEnum fromDbType(String dbType) {
        for (FieldEnum mapping : values()) {
            if (mapping.dbType.equalsIgnoreCase(dbType)) {
                return mapping;
            }
        }
        return VARCHAR;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getFormType() {
        return formType;
    }

    public String getTsType() {
        return tsType;
    }

    public static final class Type {
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
        private Type() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

    }
}

