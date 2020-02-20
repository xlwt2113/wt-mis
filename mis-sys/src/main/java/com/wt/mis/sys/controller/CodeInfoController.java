package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.BeanAccessUtil;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.core.util.VelocityKit;
import com.wt.mis.sys.entity.CodeInfo;
import com.wt.mis.sys.entity.CodeInfoItem;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.repository.CodeInfoRepository;
import com.wt.mis.sys.repository.MenuRepository;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/sys/code")
public class CodeInfoController extends BaseController<CodeInfo> {

    @Autowired
    CodeInfoRepository codeInfoRepository;

    @Autowired
    MenuRepository menuRepository;

    @Override
    public BaseRepository<CodeInfo, Long> repository() {
        return codeInfoRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/code";
    }

    //代码生成后的输出路径
    @Value("${location.templete-out-path}")
    private String templeteOutPath;


    @Override
    protected String generateSearchSql(CodeInfo codeInfo, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select * from sys_code_info  where del = 0 ");
        return sql.toString();
    }

    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    @Override
    protected String add(HttpServletRequest request, CodeInfo codeInfo) {
        this.dealItem(request,codeInfo);
        codeInfo = repository().save(codeInfo);
        return ResponseUtils.okJson("新增成功", codeInfo);
    }

    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    @Override
    protected String edit(HttpServletRequest request, @ModelAttribute("model") CodeInfo codeInfo) {
        CodeInfo tempCodeInfo = repository().findById(codeInfo.getId()).get();
        //对没有更新的属性进行赋值
        BeanAccessUtil.copyNotNullBeanProperties(codeInfo, tempCodeInfo);
        this.dealItem(request,codeInfo);
        codeInfo = repository().save(codeInfo);
        return ResponseUtils.okJson("修改成功",codeInfo);
    }

    /**
     * 根据模板生成代码
     * @param id
     * @return
     */
    @ApiOperation("生成代码模板，代码回自动生成到src目录中")
    @RequestMapping("/generate")
    @ResponseBody
    public String generateCode(@RequestParam("id") @NonNull Long id){
        CodeInfo codeInfo = codeInfoRepository.getOne(id);
        //生成菜单
        generateMenu(codeInfo);
        //生成模板
        VelocityKit.allToFile(codeInfo,templeteOutPath);
        return ResponseUtils.okJson("生成成功！",null);
    }

    /**
     * 根据配置生成菜单项目
     * @param codeInfo
     */
    private void generateMenu(CodeInfo codeInfo){
        //先删除已经存在的菜单信息
        List<Menu> menuList = menuRepository.findAllByHref(codeInfo.getUrl()+"/list");
        for(Menu menu : menuList){
            menuRepository.deleteAllByParId(menu.getId());
            menuRepository.delete(menu);
        }
        //新增菜单信息
        Menu menu = new Menu();
        menu.setTitle(codeInfo.getTitle());
        menu.setIcon("fa-th-large");
        menu.setMenuType(2);
        menu.setParId(codeInfo.getMenuId());
        menu.setHref(codeInfo.getUrl()+"/list");
        menu.setTarget("_self");
        menu.setSeq(0);
        menuRepository.save(menu);

        //增加操作信息
        Menu  listOpt = new Menu();
        listOpt.setHref("list");
        listOpt.setTitle("查询");
        listOpt.setMenuType(3);
        listOpt.setParId(menu.getId());
        listOpt.setSeq(0);
        menuRepository.save(listOpt);

        Menu  addOpt = new Menu();
        addOpt.setHref("add");
        addOpt.setTitle("添加");
        addOpt.setMenuType(3);
        addOpt.setParId(menu.getId());
        addOpt.setSeq(1);
        menuRepository.save(addOpt);

        Menu  editOpt = new Menu();
        editOpt.setHref("edit");
        editOpt.setTitle("修改");
        editOpt.setMenuType(3);
        editOpt.setParId(menu.getId());
        editOpt.setSeq(2);
        menuRepository.save(editOpt);

        Menu  deleteOpt = new Menu();
        deleteOpt.setHref("delete");
        deleteOpt.setTitle("删除");
        deleteOpt.setMenuType(3);
        deleteOpt.setParId(menu.getId());
        deleteOpt.setSeq(3);
        menuRepository.save(deleteOpt);
    }


    //处理子表数据
    private void dealItem(HttpServletRequest request,CodeInfo codeInfo){
        String[] itemId = request.getParameterValues("itemId");
        String[] poColProperty = request.getParameterValues("poColProperty");
        String[] poColName = request.getParameterValues("poColName");
        String[] required = request.getParameterValues("required");
        String[] inputType = request.getParameterValues("inputType");
        String[] poColNote = request.getParameterValues("poColNote");
        String[] conditions = request.getParameterValues("conditions");
        String[] showColName = request.getParameterValues("showColName");
        List<CodeInfoItem> codeInfoItemList = new ArrayList<CodeInfoItem>();
        for(int i=0;i<poColName.length;i++){
            CodeInfoItem codeInfoItem = new CodeInfoItem();
            codeInfoItem.setPoColProperty(poColProperty[i]);
            codeInfoItem.setPoColName(poColName[i]);
            codeInfoItem.setRequired(required[i]);
            codeInfoItem.setInputType(inputType[i]);
            codeInfoItem.setPoColNote(poColNote[i]);
            codeInfoItem.setConditions(conditions[i]);
            codeInfoItem.setShowColName(showColName[i]);
            codeInfoItem.setCodeInfo(codeInfo);
            if(StringUtils.isNotEmpty(itemId[i])){
                codeInfoItem.setId(Long.parseLong(itemId[i]));
            }
            codeInfoItemList.add(codeInfoItem);
        }
        codeInfo.setCodeInfoItemList(codeInfoItemList);
    }

}
