package com.wt.mis.sys.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;


@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "sys_role")
public class Role extends BaseEntity {

    @Column(columnDefinition = " varchar(200) COMMENT '角色名'")
    private String name;

    @Column(columnDefinition = " varchar(200) COMMENT '角色别名-英文标识'")
    private String alias;

    @Column(columnDefinition = " varchar(200) COMMENT '备注信息'")
    private String note;


}
