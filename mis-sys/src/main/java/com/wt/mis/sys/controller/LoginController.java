package com.wt.mis.sys.controller;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.repository.MenuRepository;
import com.wt.mis.sys.service.SysService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author mac
 */
@Controller
public class LoginController {

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    SysService  sysService;


    @ApiOperation("进入后台主界面")
    @GetMapping("/main")
    public ModelAndView main(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.setViewName("main");
        //获取系统名称
        view.addObject("SYSTEM_TITLE",sysService.getRegisterValue("SYSTEM_TITLE"));
        return view;
    }

    @ApiOperation("获取后台配置信息，首页，菜单等配置项目")
    @RequestMapping("/api/init")
    @ResponseBody
    public Map<String, Object> init() {
        Map<String, Object> initJson = new HashMap<>();
        Map<String, Object> clearInfo = new HashMap<>();
        Map<String, Object> homeInfo = new HashMap<>();
        Map<String, Object> logoInfo = new HashMap<>();
        Map<String, Object> menuInfo = new HashMap<>();
        //清除缓存配置
        clearInfo.put("clearUrl", "api/clear.json");
        initJson.put("clearInfo", clearInfo);
        //首页配置
        homeInfo.put("title", "首页");
        homeInfo.put("icon", "fa fa-home");
        homeInfo.put("href", "page/welcome-1.html?mpi=m-p-i-0");
        initJson.put("homeInfo", homeInfo);
        //网站logo配置
        logoInfo.put("title", sysService.getRegisterValue("SYSTEM_TITLE"));
        logoInfo.put("image", "images/logo.png");
        logoInfo.put("href", "");
        initJson.put("logoInfo", logoInfo);

        //菜单配置
        //只获取顶级菜单
        List<Menu> topMenuList = menuRepository.findByMenuTypeAndDelOrderBySeqAsc(MenuRepository.MENU_TYPE_TOP, 0);
        //获取所有菜单
        List<Menu> allMenuList = menuRepository.findAllByDel(BaseRepository.DATA_NO_DELETE);
        for (Menu menu : topMenuList) {
            menu.setChildren(this.getChildMenu(menu, allMenuList));
            menuInfo.put(String.valueOf(menu.getSeq()), menu);
        }
        initJson.put("menuInfo", menuInfo);
        return initJson;
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
                if (menuChild.getMenuType() != 2) {
                    menuChild.setChildren(getChildMenu(menuChild, menuList));
                }
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
