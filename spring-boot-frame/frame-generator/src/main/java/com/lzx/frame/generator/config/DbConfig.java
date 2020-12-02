/*
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.lzx.frame.generator.config;

import com.lzx.frame.generator.dao.*;
import com.lzx.frame.generator.utils.RRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * 数据库配置
 */
@Configuration
public class DbConfig {

    @Value("${renren.database: mysql}")
    private String database;

    private MySQLGeneratorDao mySQLGeneratorDao;

    private OracleGeneratorDao oracleGeneratorDao;

    private SQLServerGeneratorDao sqlServerGeneratorDao;

    private PostgreSQLGeneratorDao postgreSQLGeneratorDao;

    @Resource
    public void setMySQLGeneratorDao(MySQLGeneratorDao mySQLGeneratorDao) {
        this.mySQLGeneratorDao = mySQLGeneratorDao;
    }

    @Resource
    public void setOracleGeneratorDao(OracleGeneratorDao oracleGeneratorDao) {
        this.oracleGeneratorDao = oracleGeneratorDao;
    }

    @Resource
    public void setSqlServerGeneratorDao(SQLServerGeneratorDao sqlServerGeneratorDao) {
        this.sqlServerGeneratorDao = sqlServerGeneratorDao;
    }

    @Resource
    public void setPostgreSQLGeneratorDao(PostgreSQLGeneratorDao postgreSQLGeneratorDao) {
        this.postgreSQLGeneratorDao = postgreSQLGeneratorDao;
    }

    @Bean
    @Primary
    public GeneratorDao getGeneratorDao() {
        if ("mysql".equalsIgnoreCase(database)) {
            return mySQLGeneratorDao;
        } else if ("oracle".equalsIgnoreCase(database)) {
            return oracleGeneratorDao;
        } else if ("sqlserver".equalsIgnoreCase(database)) {
            return sqlServerGeneratorDao;
        } else if ("postgresql".equalsIgnoreCase(database)) {
            return postgreSQLGeneratorDao;
        } else {
            throw new RRException("不支持当前数据库：" + database);
        }
    }
}
