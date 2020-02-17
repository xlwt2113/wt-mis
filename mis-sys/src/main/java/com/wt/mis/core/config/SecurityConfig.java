package com.wt.mis.core.config;

import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wt
 * 设置登录认证及授权
 */
@Slf4j
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    AccountService accountService;

    @Autowired
    SecurityUrlFilter securityUrlFilter;
    @Autowired
    SecurityAccessDecisionManager securityAccessDecisionManager;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**", "/lib/**", "/page/**", "/index.html", "/error", "/error.html", "/files/**" ,"/");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //通过用户表动态验证
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //开启登录配置
                .authorizeRequests()
                //表示访问 /hello 这个接口，需要具备 admin 这个角色
//                .antMatchers("/sys/**").hasRole("admin111")
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {

                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setAccessDecisionManager(securityAccessDecisionManager);
                        o.setSecurityMetadataSource(securityUrlFilter);
                        return o;
                    }
                })
                //表示剩余的其他接口，登录之后就能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/index.html")
                //登录处理接口
                .loginProcessingUrl("/login")
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("username")
                //定义登录时，用户密码的 key，默认为 password
                .passwordParameter("password")
                //登录成功的处理器
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        //登录成功写入session
                        UserDetails userDetails = accountService.loadUserByUsername(authentication.getName());
                        Account loginUser = accountService.getAccountByName(userDetails.getUsername());
                        req.getSession().setAttribute("loginUser", loginUser);
                        resp.setContentType("application/text;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("success");
                        out.flush();
                    }
                })
                //登录失败的处理
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
                        resp.setContentType("application/text;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("fail");
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException e) throws IOException, ServletException {
                        log.info("访问【" + req.getRequestURL() + "】无权限");
                        //如果是ajax提交则返回json信息，否则转向无权限页面
                        if (isAjaxRequest(req)) {
                            resp.setContentType("application/text;charset=utf-8");
                            PrintWriter out = resp.getWriter();
                            out.write(ResponseUtils.forbiddenJson("无权限执行该操作", null));
                            out.flush();
                        } else {
                            resp.sendRedirect("/page/403.html");
                        }
                    }

                    /**
                     * 判断是否为ajax提交
                     * @param request
                     * @return
                     */
                    public boolean isAjaxRequest(HttpServletRequest request) {
                        if (request.getHeader("accept").indexOf("application/json") > -1
                                || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").equals(
                                "XMLHttpRequest"))) {
                            return true;
                        }
                        return false;
                    }

                })
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
                        res.sendRedirect("/index.html");
                    }

                })
                .permitAll()
                .and().cors()
                .and().headers().frameOptions().disable()
                .and().csrf().disable();
    }

}
