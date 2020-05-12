package com.exodus.orm.utils;

import com.exodus.orm.annotation.ORMColumn;
import com.exodus.orm.annotation.ORMId;
import com.exodus.orm.annotation.ORMTable;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 9:26
 * <p>
 * 用于解析实体类中的注解信息
 */
public class AnnotationUtil {
    /**
     * 获取类名信息
     *
     * @param clz
     * @return
     */
    public static String getClassName(Class clz) {
        return clz.getName();
    }

    /**
     * 获取ORMTable注解中表名信息
     *
     * @param clz
     * @return
     */
    public static String getTableName(Class clz) {
        if (clz.isAnnotationPresent(ORMTable.class)) {
            ORMTable ormTable = (ORMTable) clz.getAnnotation(ORMTable.class);
            return ormTable.name();
        } else {
            System.out.println("缺少ormTable注解信息");
            return null;
        }
    }

    /**
     * 获取主键属性和对应的字段
     *
     * @param clz
     * @return
     */
    public static Map<String, String> getIdMapper(Class clz) {
        boolean flag = true;
        Map<String, String> map = new HashMap<String, String>();
        Field[] fields = clz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ORMId.class)) {
                flag = false;
                String fieldName = field.getName();
                if (field.isAnnotationPresent(ORMColumn.class)) {
                    ORMColumn ormColumn = field.getAnnotation(ORMColumn.class);
                    String columnName = ormColumn.name();
                    map.put(fieldName, columnName);
                    break;
                } else {
                    System.out.println("缺少ORMColumn注解信息");
                }
            }
        }

        if (flag) {
            System.out.println("缺少ORMId注解信息");
        }

        return map;
    }

    /**
     * 获取类中所有属性对应的字段
     *
     * @param clz
     * @return
     */
    public static Map<String, String> getPropMapping(Class clz) {
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(getIdMapper(clz));
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            ORMColumn ormColumn = field.getAnnotation(ORMColumn.class);
            String columnName = ormColumn.name();
            String fieldName = field.getName();
            map.put(fieldName, columnName);
        }

        return map;
    }

    /**
     * 获取某包下所有的类名
     *
     * @param packagePath
     * @return
     */
    public static Set<String> getClassNameByPackage(String packagePath) {
        Set<String> names = new HashSet<String>();
        String packageFile = packagePath.replace(".", "/");
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

        if (classpath == null) {
            classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        }

        try {
            classpath = java.net.URLDecoder.decode(classpath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 解析entity package下所有的类文件名
        File dir = new File(classpath + packageFile);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.endsWith(".class")) {
                    name = packagePath + "." + name.substring(0, name.lastIndexOf("."));
                    names.add(name);
                }
            }
        } else {
            System.out.println("包路径不存在");
        }

        return names;
    }
}
