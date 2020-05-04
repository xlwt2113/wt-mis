
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.FileUtil;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Meter;
import com.wt.mis.dev.entity.MeterBox;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.repository.*;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.util.DictUtils;
import com.wt.mis.sys.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/dev/meter")
public class MeterController extends BaseController<Meter> {

    @Value("${web_upload_file_path}")
    private String baseUploadPath;
    @Autowired
    BranchBoxRepository branchBoxRepository;
    @Autowired
    MeterBoxRepository meterBoxRepository;
    @Autowired
    MeterRepository meterRepository;
    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    DevService devService;

    @Autowired
    TopologyRepository topologyRepository;

    @Override
    public BaseRepository<Meter, Long> repository() {
        return meterRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/meter";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, Meter meter) {
        super.initModel(id, model, meter);
        //初始化班组为当前登录人班组
        if(meter.getId()==null){
            meter.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
    }

    @Override
    protected String generateSearchSql(Meter meter, HttpServletRequest request) {
        Dep dep = new Dep();
        if(meter.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(meter.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_meter as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(meter.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + meter.getInstallationLocation() + "%'");
        }
        if (StringUtils.isNotEmpty(meter.getMeterBarcode())) {
            sql.append(" and t1.meter_barcode like '%" + meter.getMeterBarcode() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    /**
     * 处理列表页面中要显示的数据内容
     * @param searchResultlist
     */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for(Object obj:searchResultlist){
            HashMap<String,String> map = (HashMap) obj;
            String key = DictUtils.getDictItemKey("单/三相",String.valueOf(map.get("three_phase")));
            map.replace("three_phase",key);
        }
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        Meter meter = meterRepository.getOne(id);
        mv.addObject("threePhase",DictUtils.getDictItemKey("单/三相",String.valueOf(meter.getThreePhase())));
        mv.addObject("operationsTeam",depRespository.getOne(meter.getOperationsTeam()).getName());
        return mv;
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, Meter meterBox) {
        int cnt = meterRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        cnt = cnt + this.getOtherDevCnt(meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",meterBox);
        }
        return super.add(request, meterBox);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    protected String edit(HttpServletRequest request, Meter meterBox) {
        int cnt = meterRepository.countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(0,meterBox.getProtocolAddress(),meterBox.getOperationsTeam(),meterBox.getId());
        cnt = cnt + this.getOtherDevCnt(meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",meterBox);
        }
        List<Topology> topologyList = null;
        int[] devTypes = {4,5};
        if(meterBox.getThreePhase()==1){
            //单相
            topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,meterBox.getId(),4);
        }else{
            //三相
            topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,meterBox.getId(),5);
        }
        topologyList = topologyRepository.findAllByDelAndDevIdAndDevTypeIn(0,meterBox.getId(),devTypes);
        for(Topology topology:topologyList){
            topology.setDevName(meterBox.getOwnerName());
        }
        return super.edit(request, meterBox);
    }

    /**
     * 根据设备通讯地址及班组获取其他设备的数量
     * @param protocolAddress
     * @param operationsTeam
     * @return
     */
    private int getOtherDevCnt(String protocolAddress,long operationsTeam){
        int cnt = 0;
        cnt = cnt + branchBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + meterBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        return  cnt ;
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,6);
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
            devService.deleteDevs(ids,6);
            return ResponseUtils.okJson("删除成功",ids);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),ids);
        }
    }

    @PostMapping("/import")
    @ResponseBody
    public String importExcel(@RequestParam("file") MultipartFile upload_file){
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
            if(!ExcelUtil.checkTitle("所属用户,通讯地址,安装位置,资产编号,出厂编号,单/三相,参比电压,参比电流,电表常数,电表准确度",sheet.getRow(0))){
                result.add("导入的表格格式不正确,请核对标题列是否与列表导出的Excel文件一致！");
            }else{
                // 第一行从0开始算,先获取总行数
                int rowNumber = sheet.getLastRowNum();
                List<Meter> meterList = new ArrayList();
                Map<String,String> protocolAddressMap = new HashMap<>();
                for(int rowNum = 1;rowNum<=rowNumber;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    Meter meter = new Meter();
                    try {
                        meter.setOwnerName(row.getCell(0)!=null?row.getCell(0).getStringCellValue().trim():null);
                        meter.setProtocolAddress(row.getCell(1)!=null?row.getCell(1).toString().trim():null);
                        meter.setInstallationLocation(row.getCell(2)!=null?row.getCell(2).getStringCellValue().trim():null);
                        meter.setMeterBarcode(row.getCell(3)!=null?row.getCell(3).getStringCellValue().trim():null);
                        meter.setSerialNumber(row.getCell(4)!=null?row.getCell(4).getStringCellValue().trim():null);
                        String key = DictUtils.getDictItemKey("单/三相",row.getCell(5).getStringCellValue().trim());
                        if(StringUtils.isNotEmpty(key)){
                            meter.setThreePhase(Integer.parseInt(key));
                        }else{
                            result.add("表格第"+(rowNum + 1)+"行单/三相属性不正确！");
                        }
                        meter.setReferenceVoltage(row.getCell(6)!=null?row.getCell(6).getStringCellValue().trim():null);
                        meter.setReferenceCurrent(row.getCell(7)!=null?row.getCell(7).getStringCellValue().trim():null);
                        meter.setMeterConstant(row.getCell(8)!=null?row.getCell(7).getStringCellValue().trim():null);
                        meter.setMeterAccuracy(row.getCell(8)!=null?row.getCell(7).getStringCellValue().trim():null);

                        meter.setOperationsTeam(LoginUser.getCurrentUser().getDepId());

                        int cnt = meterRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,meter.getProtocolAddress(),meter.getOperationsTeam());
                        cnt = cnt + this.getOtherDevCnt(meter.getProtocolAddress(),meter.getOperationsTeam());
                        if(cnt>0){
                            result.add("表格第"+(rowNum + 1)+"行通讯地址["+meter.getProtocolAddress()+"]已经存在！");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        result.add("表格第"+(rowNum + 1)+"行有内容格式错误的列，请检查！");
                    }
                    meterList.add(meter);
                    protocolAddressMap.put(meter.getProtocolAddress(),meter.getProtocolAddress());
                }
                if(protocolAddressMap.size()!=meterList.size()){
                    result.add("表格中通讯地址存在重复值，请检查！");
                }
                if(result.size()<=0){
                    meterRepository.saveAll(meterList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}

