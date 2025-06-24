/*
 * Copyright (c) 2025.  little3201.
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

package io.leafage.common;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

public class MyBatisExtendSelectProvider {

    /**
     * 解析过滤条件字符串并构建查询的 sql。
     * <p>
     * 过滤条件格式示例： "age:gt:18,status:eq:active,name:like:john"
     * 每个条件由字段名、操作符和对应值组成，三者之间用冒号分隔，
     * 多个条件之间用逗号分隔。
     * <p>
     * 支持的操作符包括：
     * - eq: 等于
     * - ne: 不等于
     * - like: 模糊匹配（SQL LIKE，自动加%前后缀）
     * - gt: 大于
     * - gte: 大于等于
     * - lt: 小于
     * - lte: 小于等于
     *
     * @param clazz    结果类型
     * @param filters  条件
     * @param pageable 分页、排序
     * @return 结果
     */
    public static String dynamicQuery(Class<?> clazz, String filters, Pageable pageable) {
        Assert.notNull(pageable, "pageable parameter must not be null.");
        // 获取实体类的表名
        String tableName = getTableName(clazz);

        // 构建 SQL 语句
        SQL sql = new SQL();
        sql.SELECT("*").FROM(tableName); // 使用推断出的表名

        if (filters != null && !filters.isEmpty()) {
            String[] parts = filters.split(",");  // 根据逗号分割条件
            StringBuilder whereClause = new StringBuilder();

            for (String part : parts) {
                String[] tokens = part.trim().split(":", 3);
                if (tokens.length != 3) continue;  // 确保格式正确

                String field = tokens[0];  // 字段名
                String op = tokens[1].toLowerCase();  // 操作符
                String value = tokens[2];  // 值

                if (!whereClause.isEmpty()) {
                    whereClause.append(" AND ");
                }
                whereClause.append(field).append(" "); // 添加字段

                switch (op) {
                    case "eq":
                        whereClause.append("= #{").append(value).append("}");
                        break;
                    case "ne":
                        whereClause.append("!= #{").append(value).append("}");
                        break;
                    case "like":
                        whereClause.append("LIKE #{").append(value).append("}");
                        break;
                    case "gt":
                        whereClause.append("> #{").append(value).append("}");
                        break;
                    case "gte":
                        whereClause.append(">= #{").append(value).append("}");
                        break;
                    case "lt":
                        whereClause.append("< #{").append(value).append("}");
                        break;
                    case "lte":
                        whereClause.append("<= #{").append(value).append("}");
                        break;
                    default:
                        break;
                }
            }

            // 添加到 WHERE 子句
            if (!whereClause.isEmpty()) {
                sql.WHERE(whereClause.toString());
            }
        }

        pageable(sql, pageable);

        return sql.toString();  // 返回生成的 SQL 查询
    }

    private static void pageable(SQL sql, Pageable pageable) {
        if (pageable != null) {
            int page = pageable.getPageNumber();  // 当前页
            int size = pageable.getPageSize();    // 每页大小
            int offset = page * size;             // 计算偏移量

            // 添加分页的 OFFSET 和 LIMIT
            sql.LIMIT(size).OFFSET(offset);

            StringBuilder orderByClause = new StringBuilder();
            pageable.getSort().forEach(order -> {
                if (!orderByClause.isEmpty()) {
                    orderByClause.append(", ");
                }
                // 排序字段和方向
                orderByClause.append(order.getProperty())
                        .append(" ")
                        .append(order.isAscending() ? "ASC" : "DESC");
            });

            // 添加排序条件
            if (!orderByClause.isEmpty()) {
                sql.ORDER_BY(orderByClause.toString());
            }
        }
    }

    private static String getTableName(Class<?> clazz) {
        // 获取类上的 @Table 注解
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            return tableAnnotation.value();  // 返回 @Table 注解中的 value 属性值
        }

        // 如果没有 @Table 注解，抛出异常
        throw new IllegalStateException("The entity class " + clazz.getSimpleName() + " is missing the @Table annotation.");
    }
}
