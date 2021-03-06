package com.wt.mis.sys.controller;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.repository.DictRepository;
import com.wt.mis.sys.repository.MenuRepository;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 提供需要sys模块下相关实体或其他对象数据展现的通用api方法
 *
 * @author mac
 */
@Slf4j
@RestController
@RequestMapping("/api/sys")
public class ApiSysController {

    @Autowired
    DepRespository depRespository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    DictRepository dictRepository;

    @Autowired
    DictItemRepository dictItemRepository;


    @ApiOperation("获取字典项目的json数据")
    @RequestMapping("/dict_json/{dictName}")
    public List<Node> dictJson(@PathVariable String dictName){
        Dict dict = dictRepository.getFirstByDictNameAndDel(dictName,0);
        List<DictItem> list = dictItemRepository.getAllByDictAndDel(dict,0);
        List nodeList = new ArrayList();
        for(DictItem item:list){
            Node node = new Node(item.getItemValue(),item.getItemKey());
            nodeList.add(node);
        }
        return nodeList;
    }

    class Node{
        private String id;
        private String name;

        Node(String id ,String name){
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    @ApiOperation("获取JSON格式的机构树")
    @RequestMapping("/dep_tree_json")
    public List<Dep> depJson(HttpServletRequest request) {
        boolean open = false;
        boolean show_self = false;
        //默认的根机构ID
        long rootId = 1;

        //设置相关的机构树显示属性
        if("true".equals(request.getParameter("show_self"))){
            show_self = true;
        }
        //是否只显示当前登录人所在机构
        if(show_self){
            rootId = LoginUser.getCurrentUser().getDepId();
        }
        if ("true".equals(request.getParameter("open"))) {
            open = true;
        }
        //获取根机构
        Dep root = depRespository.findById(rootId).get();
        //默认根节点为打开状态
        root.setOpen(true);
        //获取所有没有删除的机构
        List<Dep> depList = depRespository.findAllByDel(0);
        root.setChildren(getChildDep(root, depList, open));
        //将整理好的树形结构放入list中用于前台展示
        List<Dep> list = new ArrayList<Dep>();
        list.add(root);
        return list;
    }


    /**
     * 递归获取子机构
     *
     * @param dep     父机构
     * @param depList 所有机构
     * @return childMenus 所有子菜单
     */
    private List<Dep> getChildDep(Dep dep, List<Dep> depList, boolean openState) {
        List<Dep> childList = new ArrayList<Dep>();
        for (Dep depChild : depList) {
            if (depChild.getPid().longValue() == dep.getId().longValue()) {
                depChild.setChildren(getChildDep(depChild, depList, openState));
                depChild.setOpen(openState);
                childList.add(depChild);
            }
        }
        childList.sort(new Comparator<Dep>() {
            @Override
            public int compare(Dep o1, Dep o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return childList;
    }

    @ApiOperation("获取JSON格式的菜单树")
    @RequestMapping("/menu_tree_json")
    public List<Menu> menuJson(HttpServletRequest request) {
        boolean open = false;
        //显示的菜单层级，默认到操作按钮层
        int showLevle = 3;
        if(StringUtils.isNotEmpty(request.getParameter("show_level"))){
            showLevle = Integer.parseInt(request.getParameter("show_level"));
        }
        if ("true".equals(request.getParameter("open"))) {
            open = true;
        }
        //只获取顶级菜单
        List<Menu> topMenuList = menuRepository.findByMenuTypeAndDelOrderBySeqAsc(MenuRepository.MENU_TYPE_TOP, 0);
        //获取所有菜单
        List<Menu> allMenuList = menuRepository.findAllByDel(BaseRepository.DATA_NO_DELETE);
        for (Menu tempMenu : allMenuList) {
            tempMenu.setChildren(this.getChildMenu(tempMenu, allMenuList,open,showLevle));
        }
        for(Menu menu:topMenuList){
            menu.setOpen(open);
            menu.setSpread(open);
        }
        topMenuList.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return topMenuList;
    }

    /**
     * 递归获取子菜单
     *
     * @param menu     父菜单
     * @param menuList 所有菜单
     * @return childMenus 所有子菜单
     */
    private List<Menu> getChildMenu(Menu menu, List<Menu> menuList,boolean openState,int showLevle) {
        List<Menu> childList = new ArrayList<Menu>();
        for (Menu menuChild : menuList) {
            if (menuChild.getParId().longValue() == menu.getId().longValue() && menuChild.getMenuType() <= showLevle) {
                menuChild.setChildren(getChildMenu(menuChild, menuList,openState,showLevle));
                menuChild.setOpen(openState);
                menuChild.setSpread(openState);
                childList.add(menuChild);
            }
        }
        childList.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return childList;
    }

}
