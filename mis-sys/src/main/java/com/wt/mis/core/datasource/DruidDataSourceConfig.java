package com.wt.mis.core.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author mac
 */
@Configuration
@Primary
public class DruidDataSourceConfig {
    @Bean(name = "springDruidDataSource")
    @ConfigurationProperties("spring.datasource") // 该注解可以自动注入对象的属性(对应配置文件spring.datasource下的属性)
    public DruidDataSource newsPoiDataSource() {
        return new DruidDataSource();
    }
}
