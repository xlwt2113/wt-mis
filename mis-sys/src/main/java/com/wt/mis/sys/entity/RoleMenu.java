package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "sys_role_menu")
public class RoleMenu extends BaseEntity {

    @Column(columnDefinition = " INT COMMENT '菜单Id'")
    private Long menuId;

    @Column(columnDefinition = " INT COMMENT '角色Id'")
    private Long roleId;

}
