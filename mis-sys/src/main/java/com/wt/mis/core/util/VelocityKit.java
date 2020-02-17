package com.wt.mis.core.util;


import com.wt.mis.sys.entity.CodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileWriter;

@Slf4j
public class VelocityKit {

    public final static String PRE_TEMP_PATH = "templates/generator/";   // 模板文件路径

    private static VelocityEngine velocityEngine = null;

    private static void newEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        velocityEngine = ve;
    }

    public static Template getTemplate(String templateName) {
        if (velocityEngine == null) {
            VelocityKit.newEngine();
        }
        return velocityEngine.getTemplate(templateName);
    }

    public static VelocityContext getContext() {
        return new VelocityContext();
    }

    public static void toFile(String templateName, VelocityContext ctx, String destPath) {
        try {
            File file = new File(destPath);
            File parentFile = file.getParentFile();
            if (parentFile != null && (!parentFile.exists() || (parentFile.exists() && !parentFile.isDirectory()))) {
                file.getParentFile().mkdirs();
            }
            Template template = VelocityKit.getTemplate(PRE_TEMP_PATH + templateName);
            FileWriter fw = new FileWriter(destPath);
            template.merge(ctx, fw);
            fw.flush();
        } catch (Exception e) {
            throw new RuntimeException("生成代码模板时出错：" + e.getMessage());
        }
    }

    /**
     * 所有模板统一生成
     *
     * @param codeInfo 代码生成配置数据
     */
    public static void allToFile(CodeInfo codeInfo,String templeteOutPath) {
        VelocityContext ctx = VelocityKit.getContext();
        ctx.put("codeInfo", codeInfo);
        ctx.put("mainPackage",codeInfo.getPackageName().split("\\.")[0]); //代码的一级package名称
        ctx.put("createTime", DateUtils.datePathFormat());
        for (TemplateTypeEnum typeEnum : TemplateTypeEnum.values()) {
            VelocityKit.toFile(typeEnum.getName(), ctx, getGenerateCodePath(typeEnum, codeInfo,templeteOutPath));
        }
    }

    /**
     * 生成每个文件的路径
     * @param typeEnum
     * @param codeInfo
     * @param templeteOutPath
     * @return
     */
    public static String getGenerateCodePath(TemplateTypeEnum typeEnum,CodeInfo codeInfo,String templeteOutPath){
        String javaFilePath = templeteOutPath + "/java/com/wt/mis/" + codeInfo.getPackageName().split("\\.")[0] ;
        String htmlFilePath = templeteOutPath + "/resources/templates/" + codeInfo.getPackageName().replace(".","/") ;
        String fileName = "";
        switch (typeEnum.getType()) {
            case "ENTITY":
                fileName =  javaFilePath + "/entity/" + StringUtils.toUpperCaseFirstOne(codeInfo.getPoName()) + ".java";
                break;
            case "CONTROLLER":
                fileName = javaFilePath + "/controller/" + StringUtils.toUpperCaseFirstOne(codeInfo.getPoName()) + "Controller.java";
                break;
            case "REPOSITORY":
                fileName = javaFilePath + "/repository/" + StringUtils.toUpperCaseFirstOne(codeInfo.getPoName()) + "Repository.java";
                break;
            default:
                fileName = htmlFilePath + "/" + typeEnum.getName().replace(".vm","") + ".html";

        }
        return fileName;
    }



}
