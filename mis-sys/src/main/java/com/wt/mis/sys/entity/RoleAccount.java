package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "sys_role_account")
public class RoleAccount extends BaseEntity {


    @Column(columnDefinition = " INT COMMENT '账号Id'")
    private Long accountId;

    @Column(columnDefinition = " INT COMMENT '角色Id'")
    private Long roleId;

}
