package com.exodus.orm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 15:39
 */
public class ORMSession {
    Connection connection;

    public ORMSession(Connection connection) {
        this.connection = connection;
    }

    //保存数据
    public void save(Object entity) throws Exception {
        // insert into table cols... values...
        StringBuilder insertSQL = new StringBuilder();
        //1.从ORMConfig中获取保存映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;

        //2.遍历集合，从集合中找到和entity参数对应的mapper对象
        for (Mapper mapper : mapperList) {
            if (mapper.getClazzName().equals(entity.getClass().getName())) {
                StringBuilder insertSQL1 = new StringBuilder("insert into " + mapper.getTableName() + " ( ");
                StringBuilder insertSQL2 = new StringBuilder(" ) values ( ");
                Map<String, String> idMapper = mapper.getIdMapper();
                Map<String, String> propMapper = mapper.getPropMapper();

                //3.得到当前对象的所有属性
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    //4.根据属性值得到字段名
                    String colName = propMapper.get(field.getName());
                    //5.获取属性的值
                    String colValue = field.get(entity).toString();
                    //6.拼接SQL语句
                    insertSQL1.append(colName).append(",");
                    insertSQL2.append("'").append(colValue).append("',");
                }
                insertSQL.append(insertSQL1.substring(0, insertSQL1.length() - 1));
                insertSQL.append(insertSQL2.substring(0, insertSQL2.length() - 1)).append(" );");
                break;
            }
        }

        System.out.println("miniORM-save:" + insertSQL);
        //7.通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    //根据主键进行数据删除
    public void delete(Object entity) throws Exception {
        // delete from tableName where id=id_value;
        StringBuilder delSQL = new StringBuilder("delete from");
        //1.从ORMConfig中获取保存映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;

        //2.遍历集合，从集合中找到和entity参数对应的mapper对象
        for (Mapper mapper : mapperList) {
            if (mapper.getClazzName().equals(entity.getClass().getName())) {
                //3.获取对应的mapper对象，并得到表名
                delSQL.append(mapper.getTableName()).append(" where ");

                //4.得到主键的字段名和属性名
                Object[] idProps = mapper.getIdMapper().keySet().toArray();
                Object[] idColumns = mapper.getIdMapper().values().toArray();

                //5.获取主键的值
                Field field = entity.getClass().getDeclaredField(idProps[0].toString());
                String idVal = field.get(entity).toString();

                //6.拼接sql
                delSQL.append(idColumns[0].toString()).append(" = ").append(idVal);
                break;
            }
        }
        System.out.println("miniORM-del:" + delSQL);

        //7.通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(delSQL.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    //根据主键进行查询  select * from tableName where id=idValue
    public Object findOne(Class clz, Object id) throws Exception {
        StringBuilder selectSQL = new StringBuilder("select * from ");

        //1.从ORMConfig中获取保存映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;

        //2.遍历集合，从集合中找到和entity参数对应的mapper对象
        for (Mapper mapper : mapperList) {
            if (mapper.getClazzName().equals(clz.getName())) {
                //3.获取对应的mapper对象，并得到表名
                selectSQL.append(mapper.getTableName()).append(" where ");

                //4.获取主键字段名
                Object[] idColumns = mapper.getIdMapper().values().toArray();

                //5.拼接sql
                selectSQL.append(idColumns[0].toString()).append(" = ").append(id);

                break;
            }
        }
        System.out.println("miniORM-select:" + selectSQL);

        //6.通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL.toString());
        ResultSet resultSet = preparedStatement.executeQuery();

        //7.封装结果集返回对象
        if (resultSet.next()) {
            //8.创建一个对象
            Object obj = clz.newInstance();

            //9.得到存有映射信息的集合，遍历mapperList
            for (Mapper mapper : mapperList) {
                if (mapper.getClazzName().equals(clz.getName())) {
                    //10.获取属性、字段映射信息
                    Map<String, String> propMapper = mapper.getPropMapper();
                    //11.属性赋值
                    for (String prop : propMapper.keySet()) {
                        String column = propMapper.get(prop); // column为属性对应表字段名
                        Field field = clz.getDeclaredField(prop);
                        field.setAccessible(true);
                        field.set(obj, resultSet.getObject(column));
                    }
                    break;
                }
            }
            //12.释放资源
            preparedStatement.close();
            resultSet.close();
            //13.返回封装好的查询对象
            return obj;
        } else {
            // 没有数据
            preparedStatement.close();
            resultSet.close();
            return null;
        }
    }

    //关闭连接，释放资源
    public void closeSession() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
