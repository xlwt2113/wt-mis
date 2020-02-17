package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.repository.MenuRepository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author mac
 */
@Slf4j
@Controller
@RequestMapping("/sys/menu")
public class MenuController extends BaseController<Menu> {

    @Autowired
    MenuRepository menuRepository;

    @Override
    public BaseRepository<Menu, Long> repository() {
        return menuRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/menu";
    }

    @ApiOperation("获取所有菜单信息")
    @PostMapping("/list")
    @ResponseBody
    @Override
    protected ResponseEntity list(HttpServletRequest request,
                                  Menu menu,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageCurrent,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        //只获取顶级菜单
        List<Menu> topMenuList = menuRepository.findByMenuTypeAndDelOrderBySeqAsc(MenuRepository.MENU_TYPE_TOP, 0);
        //获取所有菜单
        List<Menu> allMenuList = menuRepository.findAllByDel(BaseRepository.DATA_NO_DELETE);
        for (Menu tempMenu : allMenuList) {
            tempMenu.setChildren(this.getChildMenu(tempMenu, allMenuList));
        }
        topMenuList.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return ResponseUtils.ok("", topMenuList);
    }

    /**
     * 递归获取子菜单
     *
     * @param menu     父菜单
     * @param menuList 所有菜单
     * @return childMenus 所有子菜单
     */
    private List<Menu> getChildMenu(Menu menu, List<Menu> menuList) {
        List<Menu> childList = new ArrayList<Menu>();
        for (Menu menuChild : menuList) {
            if (menuChild.getParId().longValue() == menu.getId().longValue()) {
                menuChild.setChildren(getChildMenu(menuChild, menuList));
                childList.add(menuChild);
            }
        }
        childList.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        if (menu.getMenuType() <= 2) {
            menu.setOpen(true);
        } else {
            menu.setOpen(false);
        }
        return childList;
    }

}
