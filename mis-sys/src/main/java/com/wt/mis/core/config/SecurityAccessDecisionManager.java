package com.wt.mis.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * 根据角色判断用户是否有权限访问
 */
@Slf4j
@Component
public class SecurityAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        Set<String> needRoles = collection.stream().map(ConfigAttribute::getAttribute).collect(Collectors.toSet());
        if (needRoles.contains("ROLE_WHITE")) {
            return;
        } else if (needRoles.contains("ROLE_LOGIN") && authentication instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("未登录");
        }
        //当前请求需要的角色(包含任意一个即可)
        Set<String> hasRoles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (needRoles.contains("ROLE_LOGIN") || needRoles.stream().anyMatch(hasRoles::contains)) {
            return;
        }
        // spring security 自动捕获认证异常，前台可能看不到返回。
        throw new AccessDeniedException("您未获得该操作的授权!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
