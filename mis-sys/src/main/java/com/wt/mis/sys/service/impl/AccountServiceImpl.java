package com.wt.mis.sys.service.impl;

import com.wt.mis.core.exception.AppException;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.entity.Role;
import com.wt.mis.sys.entity.RoleAccount;
import com.wt.mis.sys.repository.AccountRepository;
import com.wt.mis.sys.repository.RoleAccountRepository;
import com.wt.mis.sys.repository.RoleRepository;
import com.wt.mis.sys.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RoleAccountRepository roleAccountRepository;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByNameAndStatusAndDel(s,0,0);
        if (account == null) {
            throw new UsernameNotFoundException("该用户不存在");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<RoleAccount> roleList = roleAccountRepository.findAllByAccountId(account.getId());
        for(RoleAccount roleAccount : roleList){
            Role role = roleRepository.findById(roleAccount.getRoleId()).get();
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+role.getAlias());
            grantedAuthorities.add(grantedAuthority);
        }
        account.setGrantedAuthorities(grantedAuthorities);
        return account;
    }

    @Override
    public void creatAccount(Account account) throws AppException{
        Account temp = accountRepository.findByNameAndDel(account.getName(),0);
        if(temp==null){
            accountRepository.save(account);
        }else{
            throw new AppException("账号名已经存在！");
        }
    }

    @Override
    public void editAccount(Account account) throws AppException{
        Account temp = accountRepository.findByNameAndDelAndIdNot(account.getName(),0,account.getId());
        if(temp==null){
            accountRepository.save(account);
        }else{
            throw new AppException("账号名已经存在！");
        }
    }

    @Override
    public Account getAccountByName(String loginName) {
        return accountRepository.findByNameAndDel(loginName,0);
    }
}
