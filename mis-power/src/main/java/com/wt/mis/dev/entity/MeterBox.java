package com.wt.mis.dev.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "transform_dev_meter_box")
@EqualsAndHashCode(callSuper = true)
@Proxy(lazy = false)
public class MeterBox extends BaseEntity {

    /**
    * 表箱名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '表箱名称'")
    private String meterBoxName;
    /**
    * 设备安装位置
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备安装位置'")
    private String installationLocation;
    /**
    * 协议通讯地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '协议通讯地址'")
    private String protocolAddress;
    /**
    * 运维班组
    */
    @Column(columnDefinition = " int(11) COMMENT '运维班组编号'")
    private Long operationsTeam;

    /**
     * 单/三相:单相：1 三相 2
     */
    @Column(columnDefinition = " int COMMENT '单/三相'")
    private Integer threePhase;

}
