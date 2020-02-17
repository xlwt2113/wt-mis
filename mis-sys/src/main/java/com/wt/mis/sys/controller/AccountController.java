package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.BeanAccessUtil;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.entity.RoleAccount;
import com.wt.mis.sys.repository.AccountRepository;
import com.wt.mis.sys.repository.RoleAccountRepository;
import com.wt.mis.sys.repository.RoleRepository;
import com.wt.mis.sys.service.AccountService;
import com.wt.mis.sys.service.RoleService;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author mac
 */
@Slf4j
@Controller
@RequestMapping("/sys/account")
public class AccountController extends BaseController<Account> {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleAccountRepository roleAccountRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    AccountService accountService;

    @Override
    public BaseRepository<Account, Long> repository() {
        return accountRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/account";
    }

    @Override
    protected String generateSearchSql(Account account, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.`name` as dep_name from sys_account t1 left join sys_dep t2  on t1.dep_id = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(account.getName())) {
            sql.append(" and t1.name like '%" + account.getName() + "%'");
        }
        if (StringUtils.isNotEmpty(account.getRealName())) {
            sql.append(" and t1.real_name like '%" + account.getRealName() + "%'");
        }
        return sql.toString();
    }

    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    @Override
    protected String add(HttpServletRequest request, Account t) {
        try{
            accountService.creatAccount(t);
            return ResponseUtils.okJson("新增成功", t);
        }catch (AppException e){
            return ResponseUtils.errorJson("新增失败:"+e.getMessage(), t);
        }

    }

    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    @Override
    public String edit(HttpServletRequest request, @ModelAttribute("model") Account t) {
        Account temp = accountRepository.findById(t.getId()).get();
        //对没有更新的属性进行赋值
        BeanAccessUtil.copyNotNullBeanProperties(t, temp);
        try{
            accountService.editAccount(t);
            return ResponseUtils.okJson("修改成功",t);
        }catch (AppException e){
            return ResponseUtils.errorJson("修改失败:"+e.getMessage(), t);
        }
    }

    @ApiOperation("打开分配角色的界面")
    @GetMapping("/role")
    protected ModelAndView openRolePage(@RequestParam("id") @NonNull Long id) {
        //添加修改都一个页面
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/role");
        //获取所有角色
        List roleList = roleRepository.findAllByDel(0);
        mv.addObject("roleList",roleList);
        //获取已经具有的角色
        List<RoleAccount> roleAccountList = roleAccountRepository.findAllByAccountId(id);
        String ids = "";
        for(RoleAccount roleAccount : roleAccountList){
            ids = ids + roleAccount.getRoleId() + ",";
        }
        mv.addObject("ids",ids);
        return mv;
    }

    @ApiOperation("保存角色")
    @PostMapping("/role")
    @ResponseBody
    protected String saveRole(HttpServletRequest request,Account account, @RequestParam("ids") List<Long> roleIds) {
        roleService.saveRoleAccount(account,roleIds);
        return ResponseUtils.okJson("修改成功", account);
    }

    @ApiOperation("打开修改密码界面")
    @GetMapping("/change_pwd")
    public ModelAndView changePwd(HttpServletRequest request){
        //添加修改都一个页面
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/change_pwd");
        return mv;
    }

    @ApiOperation("修改密码")
    @PostMapping("/change_pwd")
    @ResponseBody
    public String changePwd(@RequestParam("old_password") String oldPassword,@RequestParam("new_password") String newPassword,@RequestParam("again_password") String againPassword ){
        Account loginUser = LoginUser.getCurrentUser();
        if(!new BCryptPasswordEncoder().matches(oldPassword,loginUser.getPassword())){
            return ResponseUtils.errorJson("旧密码不正确",null);
        }
        if(!newPassword.equals(againPassword)){
            return ResponseUtils.errorJson("两次新密码不一致",null);
        }
        Account account = accountRepository.findById(loginUser.getId()).get();
        account.setPassword(new BCryptPasswordEncoder().encode(againPassword));
        accountRepository.save(account);
        return ResponseUtils.okJson("修改成功", account);
    }


}
