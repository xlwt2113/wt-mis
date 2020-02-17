package com.wt.mis.core.util;

import com.wt.mis.sys.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mac
 */
public class LoginUser {

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
        return true;
    }

}
