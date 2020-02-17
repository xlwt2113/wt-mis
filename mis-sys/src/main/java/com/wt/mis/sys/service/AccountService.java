package com.wt.mis.sys.service;

import com.wt.mis.core.exception.AppException;
import com.wt.mis.sys.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author mac
 */
public interface AccountService extends UserDetailsService {

    /**
     * 新增账号信息
     * @param account
     */
    public void creatAccount(Account account) throws AppException;

    /**
     * 修改账号信息
     * @param account
     */
    public void editAccount(Account account) throws AppException;
}
