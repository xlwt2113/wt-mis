package com.wt.mis.sys.service.impl;

import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.*;
import com.wt.mis.sys.repository.MenuRepository;
import com.wt.mis.sys.repository.RoleAccountRepository;
import com.wt.mis.sys.repository.RoleMenuRepository;
import com.wt.mis.sys.repository.RoleRepository;
import com.wt.mis.sys.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMenuRepository roleMenuRepository;

    @Autowired
    RoleAccountRepository roleAccountRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleAccount(Account account, List<Long> roleIds) {
        //删除该账号已经有的角色
        List<RoleAccount> list = roleAccountRepository.findAllByAccountId(account.getId());
        roleAccountRepository.deleteAll(list);
        //重新添加账号角色信息
        for (Long roleId : roleIds) {
            RoleAccount roleAccount = new RoleAccount();
            roleAccount.setRoleId(roleId);
            roleAccount.setAccountId(account.getId());
            roleAccountRepository.save(roleAccount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenu(Role role, List<Long> menuIds) {
        //删除该角色对应的所有菜单
        List<RoleMenu> list = roleMenuRepository.findAllByRoleId(role.getId());
        roleMenuRepository.deleteAll(list);
        //重新添加账号角色信息
        for (Long menuId : menuIds) {
            Menu menu = menuRepository.findById(menuId).get();
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            roleMenuRepository.save(roleMenu);
        }
    }

    @Override
    public List<Menu> getAllMenuForSecurity() {
        //只获取菜单
        List<Menu> menuList = menuRepository.findByMenuTypeAndDelOrderBySeqAsc(2, 0);
        List<Menu> resultMenuList = new ArrayList<>();
        for (Menu menu : menuList) {

            String baseUrl = menu.getHref();
            //将菜单地址进行调整，使其可以符合路径对比
            if (StringUtils.isNotEmpty(menu.getHref()) && menu.getHref().split("/").length > 2) {
                baseUrl = menu.getHref().substring(0, menu.getHref().lastIndexOf("/"));
            }
            //更新菜单操作路径的URLPattern
            List<Menu> childMenuList = menuRepository.findAllByParIdAndDel(menu.getId(), 0);
            //如果该菜单有操作项目的话，只按照操作项目和菜单组成的路径进行认证，否则只按菜单的路径认证
            if (childMenuList != null && childMenuList.size() > 0) {
                for (Menu child : childMenuList) {
                    child.setHref(baseUrl + "/" + child.getHref());
                    resultMenuList.add(child);
                }
            } else {
                //更新菜单路径的URLPattern
                resultMenuList.add(menu);
            }

        }
        return resultMenuList;
    }

    @Override
    public List<Role> getAllRoleByMenuId(Long menuId) {
        List<RoleMenu> roleMenuList = roleMenuRepository.findAllByMenuId(menuId);
        List<Long> ids = new ArrayList();
        for (RoleMenu roleMenu : roleMenuList) {
            ids.add(roleMenu.getRoleId());
        }
        if (ids.isEmpty()) {
            return null;
        } else {
            return roleRepository.findAllByIds(ids);
        }
    }

    @Override
    public List<RoleAccount> getAllRoleAccoutByMenuAndOpt(String menuName, String opt) {
        List allRoleAccountList = new ArrayList();
        List<Menu> menuList = menuRepository.getMenuByTtileAndHref(menuName,opt);
        if(menuList!=null&&menuList.size()>0){
            Menu menu = menuList.get(0);
            List<RoleMenu> roleMenuList=  this.roleMenuRepository.findAllByMenuId(menu.getId());
            for(RoleMenu roleMenu : roleMenuList){
                List<RoleAccount> roleAccountList = this.roleAccountRepository.findAllByRoleId(roleMenu.getRoleId());
                allRoleAccountList.addAll(roleAccountList);
            }
        }
        return allRoleAccountList;
    }
}
