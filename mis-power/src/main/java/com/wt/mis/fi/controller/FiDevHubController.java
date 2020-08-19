
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.FileUtil;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Meter;
import com.wt.mis.fi.entity.FiDevHub;
import com.wt.mis.fi.entity.FiLine;
import com.wt.mis.fi.repository.FiDevHubRepository;
import com.wt.mis.fi.repository.FiLineRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.service.SysService;
import com.wt.mis.sys.util.DictUtils;
import com.wt.mis.sys.util.ExcelUtil;
import com.wt.mis.sys.view.TreeView;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/fi/devhub")
public class FiDevHubController extends BaseController<FiDevHub> {

    @Autowired
    FiDevHubRepository fiDevHubRepository;
    @Autowired
    FiLineRepository fiLineRepository;


    @Autowired
    SysService sysService;
    @Override
    public BaseRepository<FiDevHub, Long> repository() {
        return fiDevHubRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/devhub";
    }

    @Override
    protected String generateSearchSql(FiDevHub fiDevHub, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fi_dev_hub as t1  where t1.del = 0 ");
        return sql.toString();
    }


    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, FiDevHub fiDevHub) {
        fiDevHub.setOnlineStatus(1); //默认设置为不在线
        //添加线路下的节点时不用设置lineid
        if(fiDevHub.getParentId()!=0){
            FiDevHub pdev = fiDevHubRepository.getOne(fiDevHub.getParentId());
            fiDevHub.setLineId(pdev.getLineId());
        }
        return super.add(request, fiDevHub);
    }

    @Override
    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    protected ModelAndView openViewPage(@NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        FiDevHub fiDevHub = fiDevHubRepository.getOne(id);
        if(fiDevHub.getParentId()==0){
            //获取线路信息
            FiLine fiLine = fiLineRepository.getOne(fiDevHub.getLineId());
            mv.addObject("parentName",fiLine.getLineName());
        }else{
            //获取上级节点信息
            FiDevHub pfiDevHub = fiDevHubRepository.getOne(fiDevHub.getParentId());
            mv.addObject("parentName",pfiDevHub.getHubLocation()+"["+pfiDevHub.getHubTermaddr()+"]");
        }

        return mv;
    }

    @ResponseBody
    @GetMapping("/tree")
    public List<TreeView> getTreeJson(){
        List treeViewList = new ArrayList();
        List<FiLine> lineList = fiLineRepository.findAllByDel(0);
        for(FiLine line : lineList){
            TreeView lineView = new TreeView(line.getId().toString(),line.getLineName(),true,"line");
            treeViewList.add(lineView);
            lineView.setChildren(getChildHub(line));
        }
        return treeViewList;
    }

    private List<TreeView> getChildHub(FiLine line){
        List hubViewList = new ArrayList();
        //先获取线路节点下的第一层数据
        List<FiDevHub> devList = fiDevHubRepository.findAllByLineIdAndParentIdAndDel(line.getId(),0,0);
        List<FiDevHub> alldevList = fiDevHubRepository.findAllByLineIdAndDel(line.getId().intValue(),0);
        for(FiDevHub dev : devList){
            TreeView devView = new TreeView(dev.getId().toString(),dev.getHubLocation()+"["+dev.getHubTermaddr().toString()+"]",true,"dev");
            devView.setChildren(getChildHubDg(dev,alldevList));
            hubViewList.add(devView);
        }
        return hubViewList;
    }

    /**
     * 递归获取子机构
     *
     * @param dep     父机构
     * @param depList 所有机构
     * @return childMenus 所有子菜单
     */
    private List<TreeView> getChildHubDg(FiDevHub dep, List<FiDevHub> depList) {
        List<TreeView> childList = new ArrayList();
        for (FiDevHub depChild : depList) {
            if (depChild.getParentId().longValue() == dep.getId().longValue()) {
                TreeView devView = new TreeView(depChild.getId().toString(),depChild.getHubLocation()+"["+depChild.getHubTermaddr().toString()+"]",true,"dev");
                devView.setChildren(getChildHubDg(depChild, depList));
                childList.add(devView);
            }
        }
        return childList;
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
            if(!ExcelUtil.checkTitle("终端地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址,1号测温模块地址",sheet.getRow(0))){
                result.add("导入的表格格式不正确,请核对标题列是否与模板文件一致！");
            }else{
                // 第一行从0开始算,先获取总行数
                int rowNumber = sheet.getLastRowNum();
                List<FiDevHub> devList = new ArrayList();
                for(int rowNum = 1;rowNum<=rowNumber;rowNum++){
                    Row row = sheet.getRow(rowNum);

                    try {


                    } catch (Exception e) {
                        e.printStackTrace();
                        result.add("表格第"+(rowNum + 1)+"行数据存在格式问题，请检查或将内容为数字的列转为文本格式后再试！");
                    }

                }

                if(result.size()<=0){
                    fiDevHubRepository.saveAll(devList);
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
