package com.exodus.orm.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 10:17
 * <p>
 * 用于封装和存储对象与表之间的映射信息
 */
public class Mapper {
    private String clazzName;
    private String tableName;
    // 主键信息
    private Map<String, String> idMapper = new HashMap<String, String>();
    // 普通属性字段信息
    private Map<String, String> propMapper = new HashMap<String, String>();

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(Map<String, String> idMapper) {
        this.idMapper = idMapper;
    }

    public Map<String, String> getPropMapper() {
        return propMapper;
    }

    public void setPropMapper(Map<String, String> propMapper) {
        this.propMapper = propMapper;
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "clazzName='" + clazzName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", idMapper=" + idMapper +
                ", propMapper=" + propMapper +
                '}';
    }
}
