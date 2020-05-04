package com.wt.mis.sys.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtil {

    /**
     * 根据title的字符串比较指定的某行内容是否一致
     * @param title   用逗号分隔的各个列 "台区名称,通讯地址,变压器安装位置,台区编号,变压器厂家,变压器出厂编号,变压器出厂日期,汇聚终端地址,归属线路"
     * @param row     excel中的某一行
     * @return
     */
    public static  boolean checkTitle(String title, Row row){
        String[] titles = title.split(",");
        for(int i=0;i<titles.length;i++){
            System.out.println(row.getCell(i).toString() + "=====" + titles[i]);
            if(!row.getCell(i).toString().equals(titles[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * 根据不同的excel文件，获取excel对象
     * @param excelFile
     * @return
     * @throws IOException
     */
    public static   Workbook getWorkbok(File excelFile) throws IOException {
        String EXCEL_XLS = "xls";
        String EXCEL_XLSX = "xlsx";
        Workbook wb = null;
        FileInputStream in = new FileInputStream(excelFile);
        if(excelFile.getName().toLowerCase().endsWith(EXCEL_XLS)){
            //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(excelFile.getName().toLowerCase().endsWith(EXCEL_XLSX)){
            // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

}
