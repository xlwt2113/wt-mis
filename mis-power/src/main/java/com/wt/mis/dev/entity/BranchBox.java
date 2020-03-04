package com.wt.mis.dev.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_branch_box")
@EqualsAndHashCode(callSuper = true)
public class BranchBox extends BaseEntity {

    /**
    * 分支箱名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '分支箱名称'")
    private String branchBoxName;
    /**
    * 设备安装位置
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备安装位置'")
    private String installationLocation;
    /**
    * 协议通信地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '协议通信地址'")
    private String protocolAddress;
    /**
    * 运维班组编号
    */
    @Column(columnDefinition = " int(11) COMMENT '运维班组编号'")
    private Integer operationsTeam;

}
