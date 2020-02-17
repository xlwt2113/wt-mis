package com.wt.mis.sys.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.List;

/**
 * @author 王涛
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "sys_account")
public class Account extends BaseEntity implements UserDetails {

    @Column(columnDefinition = " varchar(20) COMMENT '账号名'")
    private String name;

    @Column(columnDefinition = " varchar(20) COMMENT '真实名称'")
    private String realName;

    @Column(columnDefinition = " varchar(100) COMMENT '密码'")
    private String password;

    @Column(columnDefinition = " INT default 0  COMMENT '账号状态'")
    private int status;

    @Column(columnDefinition = " varchar(5)  COMMENT '性别'")
    private String sex;

    @Column(columnDefinition = " varchar(100)  COMMENT '头像'")
    private String headImg;

    @Column(columnDefinition = " INT COMMENT '部门ID'")
    private Long depId;

    @Transient
    List<GrantedAuthority> grantedAuthorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return this.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
