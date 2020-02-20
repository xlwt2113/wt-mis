package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.entity.Role;
import com.wt.mis.sys.entity.RoleMenu;
import com.wt.mis.sys.repository.MenuRepository;
import com.wt.mis.sys.repository.RoleMenuRepository;
import com.wt.mis.sys.repository.RoleRepository;
import com.wt.mis.sys.service.RoleService;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wt
 */
@Slf4j
@Controller
@RequestMapping("/sys/role")
public class RoleController extends BaseController<Role> {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleMenuRepository roleMenuRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    RoleService roleService;


    @Override
    public BaseRepository<Role, Long> repository() {
        return roleRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/role";
    }

    @Override
    protected String generateSearchSql(Role role, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select * from sys_role where del = 0");
        if (StringUtils.isNotEmpty(role.getName())) {
            sql.append(" and name like '%" + role.getName() + "%'");
        }
        return sql.toString();
    }

    @ApiOperation("打开分配菜单的界面")
    @GetMapping("/menu")
    protected ModelAndView openMenuPage(@RequestParam("id") @NonNull Long id) {
        //添加修改都一个页面
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/menu");
        //获取所有角色
        List roleList = roleRepository.findAllByDel(0);
        mv.addObject("roleList", roleList);

        //获取已经具有的角色
        List<RoleMenu> roleMenuList = roleMenuRepository.findAllByRoleId(id);
        String ids = "";
        for (RoleMenu roleMenu : roleMenuList) {
            try {
                Menu menu = menuRepository.getOne(roleMenu.getMenuId());
                if (menu.getMenuType() == 3) {
                    ids = ids + roleMenu.getMenuId() + ",";
                }
            } catch (EntityNotFoundException e) {
                log.error("没有找到对应的菜单对象");
                roleMenuRepository.delete(roleMenu);
            }
        }
        if (ids.length() > 0) {
            ids = ids.substring(0, ids.length() - 1);
        }
        mv.addObject("ids", "["+ids+"]");
        return mv;
    }

    @ApiOperation("保存角色对应菜单")
    @PostMapping("/menu")
    @ResponseBody
    protected String saveMenu(HttpServletRequest request, Role role, @RequestParam("ids") List<Long> menuIds) {
        roleService.saveRoleMenu(role, menuIds);
        return ResponseUtils.okJson("修改成功", role);
    }
}
