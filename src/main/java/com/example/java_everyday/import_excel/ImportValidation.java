package com.example.java_everyday.import_excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ImportValidation {
    int index();  //下标，与excel中列对应，从0开始
    boolean nullAble() default true; //是否必填，默认是必填
    String domainCode() default "";  // 字典的Code，用于字典转换
    String name() default  ""; //字典的名称，用于错误提醒
}
