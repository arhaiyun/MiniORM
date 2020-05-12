package com.exodus.orm.core;

import com.exodus.orm.utils.AnnotationUtil;
import com.exodus.orm.utils.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 10:20
 * <p>
 * 用于解析、封装框架中核心配置文件中的数据
 */
public class ORMConfig {
    private static String classpath;
    private static File cfgFile; //核心配置文件
    private static Map<String, String> propConfig; // <property> 标签中信息解析存储
    private static Set<String> mappingSet; // 存储映射配置文件路径
    private static Set<String> entitySet; // 实体类
    public static List<Mapper> mapperList; // 实体类s映射信息

    static {
        classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        // 针对文件路径进行转码
        classpath = URLDecoder.decode(classpath, StandardCharsets.UTF_8);
        cfgFile = new File(classpath + "miniORM.cfg.xml");
        if (cfgFile.exists()) {
            // 解析核心配置文件中数据
            Document document = Dom4jUtil.getXMLByFilePath(cfgFile.getPath());
            propConfig = Dom4jUtil.elements2Map(document, "property", "name");
            mappingSet = Dom4jUtil.elements2Set(document, "mapping", "resource");
            entitySet = Dom4jUtil.elements2Set(document, "entity", "package");
        } else {
            cfgFile = null;
            System.out.println("没有找到核心配置文件miniORM.cfg.xml");
        }
    }

    // 从propConfig集合中获取数据并连接数据库
    private Connection getConnection() throws Exception {
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");

        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(true);

        return connection;
    }

    private void getMapping() throws ClassNotFoundException {
        mapperList = new ArrayList<Mapper>();

        //1. 解析xxx.mapper.xml文件拿到映射数据
        for (String xmlPath : mappingSet) {
            Document document = Dom4jUtil.getXMLByFilePath(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> idMapper = Dom4jUtil.elementsID2Map(document);
            Map<String, String> propMapper = Dom4jUtil.elements2Map(document);

            Mapper mapper = new Mapper();
            mapper.setClazzName(className);
            mapper.setTableName(tableName);
            mapper.setIdMapper(idMapper);
            mapper.setPropMapper(propMapper);

            mapperList.add(mapper);
        }

        //2. 解析实体类中的注解拿到映射数据
        for (String entityPackagePath : entitySet) {
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(entityPackagePath);
            for (String name : nameSet) {
                Class aClass = Class.forName(name);
                String className = AnnotationUtil.getClassName(aClass);
                String tableName = AnnotationUtil.getTableName(aClass);
                Map<String, String> idMapper = AnnotationUtil.getIdMapper(aClass);
                Map<String, String> propMapper = AnnotationUtil.getPropMapping(aClass);

                Mapper mapper = new Mapper();
                mapper.setClazzName(className);
                mapper.setTableName(tableName);
                mapper.setIdMapper(idMapper);
                mapper.setPropMapper(propMapper);

                mapperList.add(mapper);
            }
        }
    }

    /**
     * @return
     */
    public ORMSession buildORMSession() throws Exception {
        //1.连接数据库
        Connection connection = getConnection();

        //2.得到映射数据
        getMapping();

        //3.创建ORMSession对象
        return new ORMSession(connection);
    }
}
