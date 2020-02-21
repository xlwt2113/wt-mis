package com.wt.mis.core.controller;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.BeanAccessUtil;
import com.wt.mis.core.util.ResponseUtils;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


/**
 * @author mac
 */
public abstract class BaseController<T extends BaseEntity> {

    @ApiOperation("获取实例对应的服务")
    public abstract BaseRepository<T, Long> repository();

    /**
     * 用于获取列表数据的查询service
     */
    @Autowired
    SearchService searchService;

    /**
     * 获取模板页面的前缀路径
     *
     * @return 模板页面的前缀路径
     */
    protected abstract String getUrlPrefix();


    /**
     * 获取model对象，每个方法前都会执行该方法
     *
     * @param id
     * @param model
     */
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, T t) {
        if (id != null) {
            Optional<T> opt = repository().findById(id);
            model.addAttribute("model", opt.get());
        } else {
            model.addAttribute("model", t);
        }
    }


    @GetMapping("/list")
    protected ModelAndView listPage(T t) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/list");
        mv.addObject("search", t);
        return mv;
    }

    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list")
    @ResponseBody
    protected ResponseEntity list(HttpServletRequest request,
                                  T t,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        PageResult page = searchService.findBySql(this.generateSearchSql(t, request), pageNumber, pageSize);
        dealSearchList(page.getContent());
        return ResponseUtils.ok("", page);
    }

    /**
     * 对获取到的lsit数据进行加工，一般用于对字典项目的显示处理，需要处理时重写该方法即可
     * @param searchResultlist
     */
    protected void dealSearchList(List searchResultlist){}

    /**
     * 生成查询列表的sql语句
     *
     * @param t
     * @return
     */
    @ApiOperation("生成列表页面展示数据的sql查询语句")
    protected String generateSearchSql(T t, HttpServletRequest request) {
        return "";
    }

    @ApiOperation("提交创建对象")
    @GetMapping("/add")
    protected ModelAndView openAddPage(HttpServletRequest request, T t) {
        //添加修改都一个页面
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/edit");
        return mv;
    }

    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, T t) {
        t = repository().save(t);
        return ResponseUtils.okJson("新增成功", t);
    }

    @ApiOperation("打开编辑页面")
    @GetMapping("/edit")
    protected ModelAndView openEditPage(HttpServletRequest request, @RequestParam("id") @NonNull Long id) {
        //添加修改都一个页面
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/edit");
        return mv;
    }

    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    protected String edit(HttpServletRequest request, @ModelAttribute("model") T t) {
        Optional<T> opt = repository().findById(t.getId());
        //对没有更新的属性进行赋值
        BeanAccessUtil.copyNotNullBeanProperties(t, opt.get());
        t = repository().save(t);
        return ResponseUtils.okJson("修改成功",t);
    }

    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@RequestParam("id") @NonNull Long id) {
        repository().deleteByIdOnLogic(id);
        return ResponseUtils.okJson("删除成功",id);
    }

    @ApiOperation("提交删除对象-批量删除")
    @PostMapping("/delete")
    @ResponseBody
    protected String deleteIds(@RequestParam("ids") List<Long> ids) {
        repository().deleteAllByIdsOnLogic(ids);
        return ResponseUtils.okJson("删除成功",ids);
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/view");
        return mv;
    }



}
