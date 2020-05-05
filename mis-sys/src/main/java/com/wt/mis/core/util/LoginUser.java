package com.wt.mis.core.util;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.sys.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author mac
 */
@Component
public class LoginUser {

    @Autowired
    private static SearchService searchService;

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
     * @param menu
     * @return
     */
    public static boolean hasRole(String menu){
        //SELECT * FROM sys_role_menu t1 LEFT JOIN sys_menu t2 ON t1.menu_id = t2.id LEFT JOIN sys_role_account t3 ON t1.role_id = t3.role_id
        //WHERE t2.href = '/sys/account/list' AND t3.account_id = 91
        String sql = "select * from sys_account";
        List list = searchService.findAllBySql(sql);
        if(list.isEmpty()){
            return false;
        }
        return true;
    }



}
