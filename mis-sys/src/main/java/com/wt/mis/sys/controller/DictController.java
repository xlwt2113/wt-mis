package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.repository.DictRepository;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/sys/dict")
public class DictController extends BaseController<Dict> {

    @Autowired
    DictRepository dictRepository;
    @Autowired
    DictItemRepository dictItemRepository;
    @Autowired
    SearchService searchService;

    @Override
    public BaseRepository<Dict, Long> repository() {
        return this.dictRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/dict";
    }


    @ApiOperation("获取所有字典项目")
    @PostMapping("/list")
    @ResponseBody
    @Override
    protected ResponseEntity list(HttpServletRequest request,
                                  Dict dict,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        PageResult page = searchService.findBySql(dict, this.generateSearchSql(dict, request), pageNumber, pageSize);
        return ResponseUtils.ok("", page);
    }

    @Override
    protected String generateSearchSql(Dict dict, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select * from sys_dict where del = 0");
        if (StringUtils.isNotEmpty(dict.getDictName())) {
            sql.append(" and dict_name like '%" + dict.getDictName() + "%' ");
        }
        sql.append(" order by dict_name");
        return sql.toString();
    }

    @Override
    @ApiOperation("添加一个字典项")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, Dict dict) {
        String[] itemKey = request.getParameterValues("itemKey");
        String[] itemValue = request.getParameterValues("itemValue");
        List<DictItem> itemList = new ArrayList<>();
        for (int i = 0; i < itemKey.length; i++) {
            DictItem item = new DictItem();
            item.setDict(dict);
            item.setItemKey(itemKey[i]);
            item.setItemValue(itemValue[i]);
            itemList.add(item);
        }
        dict.setDictItemList(itemList);
        dict = dictRepository.save(dict);
        return ResponseUtils.okJson("新增成功", dict);
    }

    @Override
    @ApiOperation("提交修改的字典项")
    @PostMapping("/edit")
    @ResponseBody
    protected String edit(HttpServletRequest request,  Dict dict) {
        String[] itemKey = request.getParameterValues("itemKey");
        String[] itemValue = request.getParameterValues("itemValue");
        String[] itemId = request.getParameterValues("itemId");
        List<DictItem> itemList = new ArrayList<>();
        for (int i = 0; i < itemKey.length; i++) {
            DictItem item = new DictItem();
            item.setDict(dict);
            item.setItemKey(itemKey[i]);
            item.setItemValue(itemValue[i]);
            if (!StringUtils.isEmpty(itemId[i])) {
                item.setId(Long.parseLong(itemId[i]));
            }
            itemList.add(item);
        }
        dict.setDictItemList(itemList);
        dict = dictRepository.save(dict);
        return ResponseUtils.okJson("修改成功", dict);
    }

    @ApiOperation("删除字典子表")
    @GetMapping("/delItem/{itemId}")
    @ResponseBody
    public ResponseEntity delItem(@PathVariable("itemId") @NonNull Long itemId) {
        this.dictItemRepository.deleteById(itemId);
        return ResponseUtils.ok("删除成功");
    }
}
