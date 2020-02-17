package com.wt.mis.sys.controller;

import com.wt.mis.core.util.FileUtil;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.sys.entity.UploadFiles;
import com.wt.mis.sys.repository.UploadFilesRepository;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 通用的文件上传类
 */
@Slf4j
@Controller
@RequestMapping("/sys")
public class UploadFilesController {

    @Autowired
    UploadFilesRepository uploadFilesRepository;

    @Value("${web_upload_file_path}")
    private String baseUploadPath;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile upload_file){
        if (upload_file.isEmpty()) {
            ResponseUtils.errorJson("上传失败，请选择文件！",null);
        }
        //原始文件名
        String sourceName = upload_file.getOriginalFilename();
        //新文件名
        String fileName = upload_file.getOriginalFilename();
        try {
            fileName = UUID.randomUUID().toString() + sourceName.substring(sourceName.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.errorJson("上传失败:上传文件没有后缀名，请检查！",null);
        }
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            String filePath = "/" + currentTime.getYear() + "/" + currentTime.getMonthValue() + "/" + currentTime.getDayOfMonth() + "/";
            //==============存本地===============================
            File folderPath = new File(baseUploadPath + filePath);
            if (!folderPath.exists()) {
                log.info("====建立目录====");
                FileUtil.makeDirectory(new File(baseUploadPath + filePath));
            }
            File dstFile = new File(baseUploadPath + filePath + fileName);
            //保存上传文件信息
            UploadFiles uploadFiles = new UploadFiles();
            uploadFiles.setFileName(fileName);
            uploadFiles.setFilePath(filePath);
            uploadFiles.setSourceName(sourceName);
            upload_file.transferTo(dstFile);
            uploadFilesRepository.save(uploadFiles);
            return ResponseUtils.okJson("上传成功！",uploadFiles);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseUtils.errorJson("上传失败！",null);
        } catch (MaxUploadSizeExceededException e) {
            e.printStackTrace();
            ResponseUtils.errorJson("上传失败,文件大小超出限制！",null);
        }
        return ResponseUtils.errorJson("上传失败！",null);
    }

    @ApiOperation("删除上传文件")
    @PostMapping("/upload/{id}/del")
    @ResponseBody
    public String delFile(@PathVariable("id") @NonNull Long id){
        try {
            UploadFiles files = uploadFilesRepository.getOne(id);
            //删除文件
            FileUtil.deleteFile(baseUploadPath + files.getFilePath() + files.getFileName());
            uploadFilesRepository.deleteById(files.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.errorJson("删除失败！",null);
        }
        return ResponseUtils.okJson("删除成功！",null);
    }
}
