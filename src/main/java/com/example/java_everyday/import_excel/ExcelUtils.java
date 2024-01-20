package com.example.java_everyday.import_excel;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

public class ExcelUtils {

    public static Result assembleExcelData(Class<?> entryClass, Map<Integer, String[]> excelData,
                                           Map<String, Object> domainCodes) {
        Result result = new Result();
        List<Object> returnList = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        excelData.forEach((i, cells) -> {
            Object vo = null;
            try {
                vo = entryClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Field field : entryClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ImportValidation.class)) {
                    ImportValidation annotation = field.getAnnotation(ImportValidation.class);
                    int index = annotation.index();
                    String domainCode = annotation.domainCode();
                    boolean nullAble = annotation.nullAble();
                    String name = annotation.name();
                    String cellData = cells[index].trim();
                    try {
                        if (StringUtils.isEmpty(cellData) && !nullAble) {
                            errorMsg.append("第").append(i).append("行: ").append(name).append("字段不能为空！\r\n");
                        } else if (StringUtils.isEmpty(domainCode) || StringUtils.isEmpty(cellData)) {
                            //给对应字段赋值
                            setFiled(field, vo, cellData);
                        } else {
                            //进行字典转换
                            List<Map> domains = (List<Map>) domainCodes.get(domainCode);
                            boolean match = false;
                            for (Map map : domains) {
                                if (map.get("TEXT").equals(cellData)) {
                                    //给对应字段赋值
                                    setFiled(field, vo, String.valueOf(map.get("VALUE")));
                                    match = true;
                                    break;
                                }
                            }
                            /*如果没有匹配，则转换失败*/
                            if (!match) {
                                errorMsg.append("第").append(i).append("行: ").append(name).append("字段字典值不存在！！\r\n");
                            }
                        }
                    } catch (Exception e) {
                        errorMsg.append("第").append(i).append("行: ").append(name).append("字段填写格式不正确！！\r\n");
                    }

                }
            }
            //组装LIST
            returnList.add(vo);
        });
        //如果有错误信息的话，返回错误信息，返回错误标记
        if (!errorMsg.isEmpty()) {
            result = Result.buildError();
            result.setMsg(errorMsg.toString());
        }
        //放入组装后的LIST。校验失败的字段值为空
        result.setData(returnList);

        return result;
    }


    //反射给Filed赋值
    public static void setFiled(Field filed, Object vo, String data) throws IllegalAccessException {
        //当单元格值不为空的时候才需要进行赋值操作
        if (StringUtils.isNotEmpty(data)) {
            //获取Bean 属性字段的类型
            Type fileType = filed.getGenericType();
            filed.setAccessible(true);

            if (fileType.equals(String.class)) {
                filed.set(vo, data);
            } else if (fileType.equals(int.class) || fileType.equals(Integer.class)) {
                filed.set(vo, Integer.valueOf(data));
            } else if (fileType.equals(Double.class) || fileType.equals(double.class)) {
                filed.set(vo, Double.valueOf(data));
            } else if (fileType.equals(Long.class) || fileType.equals(long.class)) {
                filed.set(vo, Long.valueOf(data));
            } else if (fileType.equals(BigDecimal.class)) {
                filed.set(vo, new BigDecimal(data));
            }
//            else if (fileType.equals(Date.class)) {
//                filed.set(vo, DateUtils.parseIso8601DateTime(data));
//            }
        }
    }

    public static void main(String[] args) throws IOException {

        var excelData = new HashMap<Integer, String[]>();
        excelData.put(0, new String[]{"name1", "11", "11.11", "11111"});
        excelData.put(1, new String[]{"name2", "12", "12.12", "22222"});
        excelData.put(2, new String[]{"name3", "13", "13.13", "33333"});

        var domainsCodes = new HashMap<String, Object>();
        domainsCodes.put("aa", 11);
        domainsCodes.put("bb", 22);
        domainsCodes.put("cc", 33);
        domainsCodes.put("dd", 44);
        /*校验并组装数据*/
        Result result = ExcelUtils.assembleExcelData(MmPartNumber.class, excelData, domainsCodes);
        System.out.println(result);
        if (result.getCode() != 0) {
//            String realPath = SpringContextHolder.getServletContext().getRealPath("/");
//            String destination = realPath + "导入错误信息.txt";
//            /*返回错误信息文件*/
//            File file = new File(destination);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            FileWriter fileWriter = new FileWriter(file);
//            fileWriter.write(result.getMsg());
//            fileWriter.close();
//            HttpServletResponse response = context.getHttpServletResponse();
//            FileDownload.fileDownload(response, realPath + "导入错误信息.txt", "导入错误信息.txt");

        } else {
//TODO BatchInsert
        }
    }

}
