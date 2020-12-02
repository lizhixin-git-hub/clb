/*
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.lzx.frame.generator.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lzx.frame.generator.utils.GenUtils;
import com.lzx.frame.generator.utils.PageUtils;
import com.lzx.frame.generator.utils.Query;
import com.lzx.frame.generator.dao.GeneratorDao;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 */
@Service
public class SysGeneratorService {

    private GeneratorDao generatorDao;

    @Resource
    public void setGeneratorDao(GeneratorDao generatorDao) {
        this.generatorDao = generatorDao;
    }

    public PageUtils queryList(Query query) {
        Page<?> page = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String, Object>> list = generatorDao.queryList(query);
        return new PageUtils(list, (int) page.getTotal(), query.getLimit(), query.getPage());
    }

    private Map<String, String> queryTable(String tableName) {
        return generatorDao.queryTable(tableName);
    }

    private List<Map<String, String>> queryColumns(String tableName) {
        return generatorDao.queryColumns(tableName);
    }

    public byte[] generatorCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils.generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
