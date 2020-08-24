package com.wt.mis.sys.service;

import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.entity.Role;
import com.wt.mis.sys.entity.RoleAccount;

import java.util.List;

/**
 * @author mac
 */
public interface RoleService {

    /**
     * 保存用户对应的角色
     * 先删除该用户已经存在的角色后重新添加
     * @param account
     * @param roleIds
     */
    void saveRoleAccount(Account account, List<Long> roleIds);

    /**
     * 保存角色对应的菜单
     * 先删除该角色已经存在的所有菜单资源后重新添加
     * @param role
     * @param menuId
     */
    void saveRoleMenu(Role role, List<Long> menuId);

    /**
     * 获取用于权限验证的菜单列表，
     * 将菜单表中类型为3和2数据组织成url，用于权限匹配
     * @return
     */
    List<Menu> getAllMenuForSecurity();

    /**
     * 根据菜单Id获取该菜单对应的所有角色
     * @param menuId
     * @return
     */
    List<Role> getAllRoleByMenuId(Long menuId);

    /**
     * 根据菜单名称及操作名获取有权限的账号
     * @param menuName
     * @param opt
     * @return
     */
    List<RoleAccount> getAllRoleAccoutByMenuAndOpt(String menuName,String opt);
}
