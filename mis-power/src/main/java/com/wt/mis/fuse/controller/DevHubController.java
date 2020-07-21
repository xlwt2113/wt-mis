
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.FileUtil;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Meter;
import com.wt.mis.fuse.entity.DevHub;
import com.wt.mis.fuse.repository.DevHubRepository;
import com.wt.mis.sys.service.SysService;
import com.wt.mis.sys.util.DictUtils;
import com.wt.mis.sys.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequestMapping("/fuse/devhub")
public class DevHubController extends BaseController<DevHub> {

    @Autowired
    DevHubRepository devHubRepository;
    @Autowired
    SysService sysService;

    @Override
    public BaseRepository<DevHub, Long> repository() {
        return devHubRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/devhub";
    }

    @Override
    protected String generateSearchSql(DevHub devHub, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fuse_dev_hub as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(devHub.getHubAddress())) {
            sql.append(" and t1.hub_address like '%" + devHub.getHubAddress() + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t1.hub_location like '%" + request.getParameter("hubName") + "%'");
        }
        return sql.toString();
    }

    @PostMapping("/import")
    @ResponseBody
    public String importExcel(@RequestParam("file") MultipartFile upload_file){
        String baseUploadPath = sysService.getRegisterValue("UPLOAD_FILE_PATH");
        List resultList = new ArrayList();
        if (upload_file.isEmpty()) {
            resultList.add("上传失败，请选择文件！");
        }
        try{
            //原始文件名
            String sourceName = upload_file.getOriginalFilename();
            //新文件名
            String fileName = upload_file.getOriginalFilename();
            try {
                fileName = UUID.randomUUID().toString() + sourceName.substring(sourceName.lastIndexOf("."));
            } catch (Exception e) {
                e.printStackTrace();
                resultList.add("上传文件没有后缀名，请检查！");
            }
            LocalDateTime currentTime = LocalDateTime.now();
            String filePath = "/" + currentTime.getYear() + "/" + currentTime.getMonthValue() + "/" + currentTime.getDayOfMonth() + "/";
            //==============存本地===============================
            File folderPath = new File(baseUploadPath + filePath);
            if (!folderPath.exists()) {
                log.info("====建立目录====");
                FileUtil.makeDirectory(new File(baseUploadPath + filePath));
            }
            File dstFile = new File(baseUploadPath + filePath + fileName);
            upload_file.transferTo(dstFile);
            resultList.addAll(this.dealExcel(dstFile));
        }catch (Exception e){
            resultList.add("上传失败:"+e.getMessage());
        }
        return ResponseUtils.okJson("上传完毕:",resultList);
    }


    /**
     * 处理上传的excel
     * @param excelFile
     * @return
     */
    public  List<String> dealExcel(File excelFile){
        List result = new ArrayList();
        try{
            Workbook wb = ExcelUtil.getWorkbok(excelFile);
            Sheet sheet = wb.getSheetAt(0);
            if(!ExcelUtil.checkTitle("汇集单元地址,汇集单元安装位置,A相熔断器地址,B相熔断器地址,C相熔断器地址",sheet.getRow(0))){
                result.add("导入的表格格式不正确,请核对标题列是否与列表导出的Excel文件一致！");
            }else{
                // 第一行从0开始算,先获取总行数
                int rowNumber = sheet.getLastRowNum();
                List<DevHub> meterList = new ArrayList();
                Map<String,String> protocolAddressMap = new HashMap<>();
                for(int rowNum = 1;rowNum<=rowNumber;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    DevHub devHub = new DevHub();
                    try {
                        devHub.setHubLocation(row.getCell(1)!=null?row.getCell(1).getStringCellValue().trim():null);

                        devHub.setHubAddress(row.getCell(0)!=null?row.getCell(0).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", devHub.getHubAddress())){
                            result.add("表格第"+(rowNum + 1)+"行汇集单元格式不正确！");
                        }

                        devHub.setFuseAaddress(row.getCell(2)!=null?row.getCell(2).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", devHub.getFuseAaddress())){
                            result.add("表格第"+(rowNum + 1)+"行A相熔断器地址格式不正确！");
                        }

                        devHub.setFuseBaddress(row.getCell(3)!=null?row.getCell(3).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", devHub.getFuseBaddress())){
                            result.add("表格第"+(rowNum + 1)+"行B相熔断器地址格式不正确！");
                        }

                        devHub.setFuseCaddress(row.getCell(4)!=null?row.getCell(4).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", devHub.getFuseCaddress())){
                            result.add("表格第"+(rowNum + 1)+"行C相熔断器地址格式不正确！");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        result.add("表格第"+(rowNum + 1)+"行数据存在格式问题，请检查或将内容为数字的列转为文本格式后再试！");
                    }
                    meterList.add(devHub);
//                    protocolAddressMap.put(meter.getProtocolAddress(),meter.getProtocolAddress());
                }
//                if(protocolAddressMap.size()!=meterList.size()){
//                    result.add("表格中通讯地址存在重复值，请检查！");
//                }
                if(result.size()<=0){
                    devHubRepository.saveAll(meterList);
                }
            }
        }catch (NullPointerException e1){
            result.add("上传文件非标准Excel格式");
        }catch (NotOLE2FileException e1){
            result.add("上传文件非标准Excel格式");
        }catch (Exception e){
            e.printStackTrace();
            result.add("上传出现错误：" + e.getMessage());
        }
        return result;
    }


}
