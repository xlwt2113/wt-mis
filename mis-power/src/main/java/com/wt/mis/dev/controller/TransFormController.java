
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.*;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.*;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.service.SysService;
import com.wt.mis.sys.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequestMapping("/dev/transform")
public class TransFormController extends BaseController<TransForm> {

    @Autowired
    BranchBoxRepository branchBoxRepository;
    @Autowired
    MeterBoxRepository meterBoxRepository;
    @Autowired
    MeterRepository meterRepository;
    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    TopologyRepository topologyRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    DevService devService;

    @Autowired
    SysService sysService;

    @Override
    public BaseRepository<TransForm, Long> repository() {
        return transFormRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/transform";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, TransForm transForm) {
        super.initModel(id, model, transForm);
        //初始化班组为当前登录人班组
        if(transForm.getId()==null){
            transForm.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
        model.addAttribute("lines", lineRepository.findAllByOperationsTeam(LoginUser.getCurrentUser().getDepId()));
    }

    @Override
    protected String generateSearchSql(TransForm transForm, HttpServletRequest request) {
        Dep dep = new Dep();
        if(transForm.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(transForm.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name ,t3.line_name from transform_dev_transform as t1 " +
                " left join  sys_dep as t2 on t1.operations_team = t2.id " +
                " left join transform_dev_line as t3 on t1.line_id = t3.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(transForm.getTransformName())) {
            sql.append(" and t1.transform_name like '%" + transForm.getTransformName() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getTransformNum())) {
            sql.append(" and t1.transform_num like '%" + transForm.getTransformNum() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getDevAddress())) {
            sql.append(" and t1.dev_address like '%" + transForm.getDevAddress().toUpperCase() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getProtocolAddress())) {
            sql.append(" and t1.protocol_address like '%" + transForm.getProtocolAddress().toLowerCase() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        TransForm dev = transFormRepository.getOne(id);
        mv.addObject("operationsTeam",depRespository.getOne(dev.getOperationsTeam()).getName());
        return mv;
    }

    @ApiOperation("打开台区汇总界面")
    @GetMapping("/view_detail")
    public ModelAndView openViewDetailPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/detail");
        TransForm dev = transFormRepository.getOne(id);
        //获取当前台区总设备数
        List allDevList = topologyRepository.findAllByDelAndTransformId(0,dev.getId());
        mv.addObject("allDevCnt",allDevList.size());
        //获取分支箱数量
        List branchBoxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,3,dev.getId());
        mv.addObject("branchBoxCnt",branchBoxList.size());
        //获取单相表箱数量
        List meterBoxDxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,4,dev.getId());
        mv.addObject("meterBoxDxCnt",meterBoxDxList.size());
        //获取三相表箱数量
        List meterBoxSxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,5,dev.getId());
        mv.addObject("meterBoxSxCnt",meterBoxSxList.size());
        //获取单相电能表数量
        List meterDxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,6,dev.getId());
        mv.addObject("meterDxCnt",meterDxList.size());
        //获取三相电能表数量
        List meterSxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,7,dev.getId());
        mv.addObject("meterSxCnt",meterSxList.size());
        //获取设备在线数量
        List onLineList = topologyRepository.findAllByDelAndDevOnlineAndTransformId(0,0,dev.getId());
        mv.addObject("onLine",((onLineList.size()*100/allDevList.size()))+ "%" );
        //改台区报警设备数
        List list = searchService.findAllBySql("select count(*) as cnt from transform_dev_topology where del = 0 and transform_id = "+dev.getId()+" and CONCAT_WS('-',dev_id,dev_type) in (" +
                "SELECT CONCAT_WS('-',dev_id,dev_type) FROM transform_event_power_outage where del = 0 and history = 0 and power_status = 1 " +
                ")");
        mv.addObject("alarmCnt",((HashMap) list.get(0)).get("cnt"));

        mv.addObject("operationsTeam",depRespository.getOne(dev.getOperationsTeam()).getName());
        mv.setViewName(this.getUrlPrefix()+"/detail");

        return mv;
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    protected String edit(HttpServletRequest request, TransForm transForm) {
        //验证是否有汇聚单元地址相同的台区，有的话不允许添加
        int cnt = transFormRepository.countAllByDelAndDevAddressAndIdNot(0,transForm.getDevAddress(),transForm.getId());
        if(cnt>0){
            return ResponseUtils.errorJson("汇聚单元地址已经存在！",transForm);
        }
        cnt = transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(0,transForm.getProtocolAddress(),transForm.getOperationsTeam(),transForm.getId());
        cnt = cnt + this.getOtherDevCnt(transForm.getProtocolAddress(),transForm.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",transForm);
        }
        List<Topology> topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,transForm.getId(),2);
        for(Topology topology:topologyList){
            topology.setDevName(transForm.getTransformName());
        }
        return super.edit(request, transForm);
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, TransForm transForm) {
        //验证是否有汇聚单元地址相同的台区，有的话不允许添加
        int cnt = transFormRepository.countAllByDelAndDevAddress(0,transForm.getDevAddress());
        if(cnt>0){
            return ResponseUtils.errorJson("汇聚单元地址已经存在！",transForm);
        }
        cnt = transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,transForm.getProtocolAddress(),transForm.getOperationsTeam());
        cnt = cnt + this.getOtherDevCnt(transForm.getProtocolAddress(),transForm.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",transForm);
        }
        return super.add(request, transForm);
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
        cnt = cnt + meterRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        return  cnt ;
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,2);
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
            devService.deleteDevs(ids,2);
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
            if(!ExcelUtil.checkTitle("台区名称,通讯地址,变压器安装位置,台区编号,变压器厂家,变压器出厂编号,变压器出厂日期,汇聚终端地址,归属线路",sheet.getRow(0))){
                result.add("导入的表格格式不正确,请核对标题列是否与列表导出的Excel文件一致！");
            }else{
                // 第一行从0开始算,先获取总行数
                int rowNumber = sheet.getLastRowNum();
                List<TransForm> transFormList = new ArrayList();
                Map<String,String> protocolAddressMap = new HashMap<>();
                Map<String,String> devAddressMap = new HashMap<>();
                for(int rowNum = 1;rowNum<=rowNumber;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    TransForm transForm = new TransForm();
                    try {
                        transForm.setTransformName(row.getCell(0)!=null?row.getCell(0).getStringCellValue().trim():null);
                        transForm.setProtocolAddress(row.getCell(1)!=null?row.getCell(1).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{12}$", transForm.getProtocolAddress())){
                            result.add("表格第"+(rowNum + 1)+"行通讯地址格式不正确！");
                        }
                        transForm.setInstallationLocation(row.getCell(2)!=null?row.getCell(2).getStringCellValue().trim():null);
                        transForm.setTransformNum(row.getCell(3)!=null?row.getCell(3).toString().trim():null);
                        transForm.setTransformFactory(row.getCell(4)!=null?row.getCell(4).getStringCellValue().trim():null);
                        transForm.setSerialNumber(row.getCell(5)!=null?row.getCell(5).toString().trim():null);
                        transForm.setManufacturingDate(row.getCell(6)!=null?row.getCell(6).getDateCellValue():null);
                        transForm.setDevAddress(row.getCell(7)!=null?row.getCell(7).toString().trim():null);
                        if(!Pattern.matches("^[A-Z0-9]{8}$", transForm.getDevAddress())){
                            result.add("表格第"+(rowNum + 1)+"行汇聚终端地址格式不正确！");
                        }
                        transForm.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
                        if(row.getCell(8)!=null){
                            List<Line> lineList = lineRepository.getAllByLineName(row.getCell(8).getStringCellValue().trim());
                            if(lineList==null||lineList.size()<=0){
                                result.add("表格第"+(rowNum + 1)+"行线路名称："+row.getCell(8).getStringCellValue()+"不存在！");
                            }else{
                                transForm.setLineId(lineList.get(0).getId());
                            }
                        }else{
                            result.add("表格第"+(rowNum + 1)+"行线路名称为空！");
                        }


                        int cnt = transFormRepository.countAllByDelAndDevAddress(0,transForm.getDevAddress());
                        if(cnt>0){
                            result.add("表格第"+(rowNum + 1)+"行汇聚单元地址["+transForm.getDevAddress()+"]已经存在！");
                        }
                        cnt = transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,transForm.getProtocolAddress(),transForm.getOperationsTeam());
                        cnt = cnt + this.getOtherDevCnt(transForm.getProtocolAddress(),transForm.getOperationsTeam());
                        if(cnt>0){
                            result.add("表格第"+(rowNum + 1)+"行通讯地址["+transForm.getProtocolAddress()+"]已经存在！");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        result.add("表格第"+(rowNum + 1)+"行数据存在格式问题，请检查或将内容为数字的列转为文本格式后再试！");
                    }
                    transFormList.add(transForm);
                    protocolAddressMap.put(transForm.getProtocolAddress(),transForm.getProtocolAddress());
                    devAddressMap.put(transForm.getDevAddress(),transForm.getDevAddress());
                }
                if(protocolAddressMap.size()!=transFormList.size()){
                    result.add("表格中通讯地址存在重复值，请检查！");
                }
                if(devAddressMap.size()!=transFormList.size()){
                    result.add("表格中汇聚终端地址存在重复值，请检查！");
                }
                if(result.size()<=0){
                    transFormRepository.saveAll(transFormList);
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

