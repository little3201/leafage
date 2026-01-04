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

import static top.leafage.hypervisor.constants.FieldConstant.*;

/**
 * 数据库类型映射到Java类型、组件名和TypeScript类型
 *
 * @author wq li
 */
public enum FieldTypeEnum {

    INT2("int2", JAVA_SHORT, FORM_INPUT_NUMBER, TYPE_NUMBER),
    INT4("int4", JAVA_INTEGER, FORM_INPUT_NUMBER, TYPE_NUMBER),
    INT8("int8", JAVA_LONG, FORM_SELECT, TYPE_NUMBER),
    FLOAT4("float4", JAVA_FLOAT, FORM_INPUT_NUMBER, TYPE_NUMBER),
    FLOAT8("float8", JAVA_DOUBLE, FORM_INPUT_NUMBER, TYPE_NUMBER),
    NUMERIC("numeric", JAVA_BIGDECIMAL, FORM_INPUT_NUMBER, TYPE_NUMBER),
    BOOL("bool", JAVA_BOOLEAN, FORM_SWITCH, TYPE_BOOLEAN),
    VARCHAR("varchar", JAVA_STRING, FORM_INPUT, TYPE_STRING),
    INET("inet", JAVA_INETADDRESS, FORM_INPUT, TYPE_STRING),
    TEXT("text", JAVA_STRING, FORM_INPUT, TYPE_STRING),
    DATE("date", JAVA_LOCALDATE, FORM_DATE_PICKER, TYPE_DATE),
    TIME("time", JAVA_LOCALTIME, FORM_TIME_PICKER, TYPE_DATE),
    TIMESTAMP("timestamp", JAVA_LOCALDATETIME, FORM_DATE_PICKER, TYPE_DATE),
    TIMESTAMPTZ("timestamptz", JAVA_LOCALDATETIME, FORM_DATE_PICKER, TYPE_DATE),
    JSON("json", JAVA_STRING, FORM_INPUT, TYPE_STRING);

    private final String dbType;
    private final String javaType;
    private final String formType;
    private final String tsType;

    FieldTypeEnum(String dbType, String javaType, String formType, String tsType) {
        this.dbType = dbType;
        this.javaType = javaType;
        this.formType = formType;
        this.tsType = tsType;
    }

    public static FieldTypeEnum fromDbType(String dbType) {
        for (FieldTypeEnum mapping : values()) {
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
}

