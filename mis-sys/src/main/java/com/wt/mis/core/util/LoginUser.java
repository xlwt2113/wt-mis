package com.wt.mis.core.util;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.sys.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author mac
 */
@Component
public class LoginUser {

    @Autowired
    private SearchService searchService;

    public static LoginUser loginUser;


    @PostConstruct
    public void init() {
        loginUser = this;
        loginUser.searchService = searchService;
    }

    /**
     * 获取当前登陆用户
     * @return
     */
    public static Account getCurrentUser() {
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (Account) request.getSession().getAttribute("loginUser");
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 根据菜单判断当前用户是否有权限
     * @param urlOrMenuName 菜单地址或菜单名称
     * @param optName 功能操作名
     * @return
     */
    public static boolean hasRole(String urlOrMenuName ,String optName){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Account account = (Account) request.getSession().getAttribute("loginUser");
        String sql = "SELECT t1.* from " +
                " sys_role_menu t1" +
                " LEFT JOIN ( SELECT * FROM sys_menu WHERE par_id = ( SELECT id FROM sys_menu  WHERE href = '"+urlOrMenuName+"' or title = '"+urlOrMenuName+"' ) ) t2 ON t1.menu_id = t2.id" +
                " LEFT JOIN sys_role_account t3 ON t1.role_id = t3.role_id " +
                " WHERE" +
                " t2.href = '"+optName+"' AND t3.account_id = " + account.getId();
        List list = loginUser.searchService.findAllBySql(sql);
        if(list.isEmpty()){
            return false;
        }
        return true;
    }



}
