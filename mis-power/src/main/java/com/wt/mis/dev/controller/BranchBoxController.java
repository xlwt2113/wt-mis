
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.FileUtil;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.BranchBox;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.BranchBox;
import com.wt.mis.dev.repository.*;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.service.SysService;
import com.wt.mis.sys.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequestMapping("/dev/branchbox")
public class BranchBoxController extends BaseController<BranchBox> {

    @Autowired
    SysService sysService;
    @Autowired
    BranchBoxRepository branchBoxRepository;
    @Autowired
    MeterBoxRepository meterBoxRepository;
    @Autowired
    TransFormRepository transFormRepository;
    @Autowired
    MeterRepository meterRepository;
    @Autowired
    DepRespository depRespository;
    @Autowired
    DevService devService;
    @Autowired
    TopologyRepository topologyRepository;

    @Override
    public BaseRepository<BranchBox, Long> repository() {
        return branchBoxRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/branchbox";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, BranchBox branchBox) {
        super.initModel(id, model, branchBox);
        //初始化班组为当前登录人班组
        if(branchBox.getId()==null){
            branchBox.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
    }

    @Override
    protected String generateSearchSql(BranchBox branchBox, HttpServletRequest request) {
        Dep dep = new Dep();
        if(branchBox.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(branchBox.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from transform_dev_branch_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(branchBox.getBranchBoxName())) {
            sql.append(" and t1.branch_box_name like '%" + branchBox.getBranchBoxName() + "%'");
        }
        if (StringUtils.isNotEmpty(branchBox.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + branchBox.getInstallationLocation() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, BranchBox branchBox) {
        int cnt = branchBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,branchBox.getProtocolAddress(),branchBox.getOperationsTeam());
        cnt = cnt + this.getOtherDevCnt(branchBox.getProtocolAddress(),branchBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",branchBox);
        }
        return super.add(request, branchBox);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    protected String edit(HttpServletRequest request, BranchBox branchBox) {
        int cnt = branchBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(0,branchBox.getProtocolAddress(),branchBox.getOperationsTeam(),branchBox.getId());
        cnt = cnt + this.getOtherDevCnt(branchBox.getProtocolAddress(),branchBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",branchBox);
        }
        List<Topology> topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,branchBox.getId(),3);
        for(Topology topology:topologyList){
            topology.setDevName(branchBox.getBranchBoxName());
        }
        return super.edit(request, branchBox);
    }

    /**
     * 根据设备通讯地址及班组获取其他设备的数量
     * @param protocolAddress
     * @param operationsTeam
     * @return
     */
    private int getOtherDevCnt(String protocolAddress,long operationsTeam){
        int cnt = 0;
        cnt = cnt + meterBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + meterRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        return  cnt ;
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        BranchBox branchBox = branchBoxRepository.getOne(id);
        mv.addObject("operationsTeam",depRespository.getOne(branchBox.getOperationsTeam()).getName());
        return mv;
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,3);
            return ResponseUtils.okJson("删除成功",id);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),id);
        }
    }

    @Override
    @ApiOperation("提交删除对象-批量删除")
    @PostMapping("/delete")
    @ResponseBody
    protected String deleteIds(@RequestParam("ids") List<Long> ids) {
        try{
            devService.deleteDevs(ids,3);
            return ResponseUtils.okJson("删除成功",ids);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),ids);
        }
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
            if(!ExcelUtil.checkTitle("分支箱名称,通讯地址,安装位置",sheet.getRow(0))){
                result.add("导入的表格格式不正确,请核对标题列是否与列表导出的Excel文件一致！");
            }else{
                // 第一行从0开始算,先获取总行数
                int rowNumber = sheet.getLastRowNum();
                List<BranchBox> branchBoxList = new ArrayList();
                Map<String,String> protocolAddressMap = new HashMap<>();
                for(int rowNum = 1;rowNum<=rowNumber;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    BranchBox branchBox = new BranchBox();
                    try {
                        branchBox.setBranchBoxName(row.getCell(0)!=null?row.getCell(0).getStringCellValue().trim():null);
                        branchBox.setProtocolAddress(row.getCell(1)!=null?row.getCell(1).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", branchBox.getProtocolAddress())){
                            result.add("表格第"+(rowNum + 1)+"行通讯地址格式不正确！");
                        }
                        branchBox.setInstallationLocation(row.getCell(2)!=null?row.getCell(2).getStringCellValue().trim():null);
                        branchBox.setOperationsTeam(LoginUser.getCurrentUser().getDepId());

                        int cnt = branchBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,branchBox.getProtocolAddress(),branchBox.getOperationsTeam());
                        cnt = cnt + this.getOtherDevCnt(branchBox.getProtocolAddress(),branchBox.getOperationsTeam());
                        if(cnt>0){
                            result.add("表格第"+(rowNum + 1)+"行通讯地址["+branchBox.getProtocolAddress()+"]已经存在！");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        result.add("表格第"+(rowNum + 1)+"行数据存在格式问题，请检查或将内容为数字的列转为文本格式后再试！");
                    }
                    branchBoxList.add(branchBox);
                    protocolAddressMap.put(branchBox.getProtocolAddress(),branchBox.getProtocolAddress());
                }
                if(protocolAddressMap.size()!=branchBoxList.size()){
                    result.add("表格中通讯地址存在重复值，请检查！");
                }
                if(result.size()<=0){
                    branchBoxRepository.saveAll(branchBoxList);
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

