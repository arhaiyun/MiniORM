package com.exodus.orm.utils;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 9:17
 * <p>
 * 自定义ORM框架下基于Dom4j的工具类
 */
public class Dom4jUtil {

    /**
     * 通过文件的路径获取xml的document对象
     *
     * @param path
     * @return 返回文档对象
     */
    public static Document getXMLByFilePath(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return document;
    }

    /**
     * 获取文档中某个元素内某属性的值和元素的文本信息
     *
     * @param document
     * @param eleName
     * @param attrName
     * @return
     */
    public static Map<String, String> elements2Map(Document document, String eleName, String attrName) {
        List<Element> propList = document.getRootElement().elements(eleName);
        Map<String, String> propConfig = new HashMap<String, String>();
        for (Element element : propList) {
            String key = element.attribute(attrName).getValue();
            String value = element.getTextTrim();
            propConfig.put(key, value);
        }
        return propConfig;
    }

    /**
     * 用于解析mapper.xml文件，获取映射信息并保存到map中
     *
     * @param document
     * @return
     */
    public static Map<String, String> elements2Map(Document document) {
        Element clazzElement = document.getRootElement().element("class");
        Map<String, String> mapping = new HashMap<String, String>();

        Element idElement = clazzElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        mapping.put(idKey, idValue);

        List<Element> propElements = clazzElement.elements("property");
        for (Element element : propElements) {
            String propKey = idElement.attribute("name").getValue();
            String propValue = idElement.attribute("column").getValue();
            mapping.put(propKey, propValue);
        }
        return mapping;
    }

    /**
     * 解析mapper.xml 获取主键的映射信息并保存到map集合中
     *
     * @param document
     * @return
     */
    public static Map<String, String> elementsID2Map(Document document) {
        Element clazzElement = document.getRootElement().element("class");
        Map<String, String> mapping = new HashMap<String, String>();

        Element idElement = clazzElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        mapping.put(idKey, idValue);

        return mapping;
    }

    /**
     * 获取文档中元素内指定属性的值集合
     *
     * @param document
     * @param elementName
     * @param attrName
     * @return
     */
    public static Set<String> elements2Set(Document document, String elementName, String attrName) {
        List<Element> mappingList = document.getRootElement().elements(elementName);
        Set<String> mappingSet = new HashSet<String>();
        for (Element element : mappingList) {
            String value = element.attribute(attrName).getValue();
            mappingSet.add(value);
        }
        return mappingSet;
    }

    /**
     * 获取文档中元素内指定属性的值 - 单个属性值情况
     *
     * @param document
     * @param elementName
     * @param attrName
     * @return
     */
    public static String getPropValue(Document document, String elementName, String attrName) {
        Element element = (Element) document.getRootElement().elements(elementName).get(0);
        return element.attribute(attrName).getValue();
    }
}
