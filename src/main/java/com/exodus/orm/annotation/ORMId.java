package com.exodus.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author arhaiyun
 * @version 1.0
 * @date 2020/5/11 9:10
 */

@Retention(RetentionPolicy.RUNTIME) // 运行期间保留注解信息
@Target(ElementType.FIELD)   // 设置注解作用位置
public @interface ORMId {
}
