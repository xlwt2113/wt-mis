package com.wt.mis.core.util;

import com.wt.mis.sys.entity.UploadFiles;
import com.wt.mis.sys.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileUtil {


    @Autowired
    SysService sysService;
    public static FileUtil fileUtil;

    @PostConstruct
    public void init(){
        fileUtil = this;
    }


    /**
     * 复制文件
     *
     * @param src
     * @param dst
     * @throws Exception
     */
    public static void copy(File src, File dst) throws Exception {
        int BUFFER_SIZE = 4096;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }

    /**
     * 创建指定的目录。 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。
     * 注意：可能会在返回false的时候创建部分父目录。
     *
     * @param file
     * @return
     */
    public static void makeDirectory(File file) {
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 判断指定的文件是否存在。
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        return new File(fileName).isFile();
    }

    /**
     * 删除一个文件。
     *
     * @param filename
     * @throws IOException
     */
    public static void deleteFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.isDirectory()) {
            throw new IOException("IOException -> BadInputException: not a file.");
        }
        if (!file.exists()) {
            throw new IOException("IOException -> BadInputException: file is not exist.");
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete file. filename = " + filename);
        }
    }

    public static String getFilesHtml(String fileId,String inputFileId){
        StringBuffer html = new StringBuffer("");
        if(StringUtils.isNotEmpty(fileId)){
            String[] ids = fileId.split(",");
            List<Long> idList = new ArrayList<>();
            for(String id :ids){
                idList.add(Long.parseLong(id));
            }
            List<UploadFiles> uploadFilesList =  fileUtil.sysService.getUploadFilesByIds(idList);
            for(UploadFiles uploadFiles : uploadFilesList){
                html.append("<div><a target=\"_blank\" href=\""+uploadFiles.getFilePath() + uploadFiles.getFileName() +"\">"+uploadFiles.getSourceName()+"</a> <a onclick=\"delRow(this,'"+inputFileId+"','"+uploadFiles.getId()+"')\" href=\"javascript:void(0)\"><i class=\"layui-icon layui-icon-close layui-bg-red\"></i></a><hr class=\"layui-bg-green\"></div>");
            }
        }
        return html.toString();
    }
}
