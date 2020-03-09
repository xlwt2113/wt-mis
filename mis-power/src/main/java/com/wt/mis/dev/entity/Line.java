package com.wt.mis.dev.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_line")
@EqualsAndHashCode(callSuper = true)
public class Line extends BaseEntity {

    /**
    * 线路名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '线路名称'")
    private String lineName;
    /**
    * 线路编号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '线路编号'")
    private String lineNum;
    /**
    * 电压等级
    */
    @Column(columnDefinition = " varchar(100) COMMENT '电压等级'")
    private String voltageLevel;
    /**
    * 运维班组
    */
    @Column(columnDefinition = " int(11) COMMENT '运维班组编号'")
    private Long operationsTeam;

}
