package com.wt.mis.core.config;

import com.wt.mis.sys.entity.Menu;
import com.wt.mis.sys.entity.Role;
import com.wt.mis.sys.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author mac
 * 根据路径获取路径对应的角色
 *
 */
@Slf4j
@Component
public class SecurityUrlFilter implements FilterInvocationSecurityMetadataSource {

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    RoleService roleService;



    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        List<Menu> menuList = roleService.getAllMenuForSecurity();
        String requestUrl = ((FilterInvocation) o).getRequestUrl().split("\\?")[0];
        for(Menu menu : menuList){
//            log.info("菜单路径："+menu.getHref());
//            log.info("访问的路径："+requestUrl);
            if(antPathMatcher.match(menu.getHref(),requestUrl)){
                log.info("匹配成功！");
                List<Role> roleList = roleService.getAllRoleByMenuId(menu.getId());
                if(roleList!=null){
                    String[] roleStr = new String[roleList.size()];
                    String roleStr2 = "";
                    for(int i=0 ; i<roleList.size();i++){
                        roleStr[i] = "ROLE_"+roleList.get(i).getAlias();
                        roleStr2 = roleStr2 + "; "+ roleList.get(i).getAlias();
                    }
                    log.info("能访问该路径的角色："+roleStr2);
                    return SecurityConfig.createList(roleStr);
                }else{
                    //匹配到了菜单，却没有找到分配的角色，该菜单默认分配一个不可访问的角色
                   return  SecurityConfig.createList("ROLE_NO");
                }
            }
//            log.info("=============");
        }
        //如果和所有菜单都不匹配，则设置为登录后就可以访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
