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

package io.leafage.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.MyBatisJdbcConfiguration;

import javax.sql.DataSource;

/**
 * Mybatis configuration
 *
 * @author wq li
 */
@Configuration(proxyBeanMethods = false)
@Import(MyBatisJdbcConfiguration.class)
@MapperScan(basePackages = "io.leafage.*.mapper")
public class MyBatisConfiguration {

    @Bean
    SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        // Configure MyBatis here
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 创建 MyBatis 配置对象
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 设置驼峰命名转换
        configuration.setMapUnderscoreToCamelCase(true);

        // 将配置对象设置到 SqlSessionFactoryBean 中
        factoryBean.setConfiguration(configuration);
        return factoryBean;
    }

    /**
     * 创建 SqlSessionTemplate，线程安全的 SqlSession
     *
     * @param sqlSessionFactory SqlSession Factory
     * @return SqlSessionTemplate
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
