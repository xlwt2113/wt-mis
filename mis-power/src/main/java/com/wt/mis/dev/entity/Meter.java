package com.wt.mis.dev.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_meter")
@EqualsAndHashCode(callSuper = true)
public class Meter extends BaseEntity {

    /**
    * 所属用户
    */
    @Column(columnDefinition = " varchar(100) COMMENT '所属用户'")
    private String ownerName;
    /**
    * 设备安装位置
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备安装位置'")
    private String installationLocation;
    /**
    * 资产编号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '资产编号'")
    private String meterBarcode;
    /**
    * 出厂编号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '出厂编号'")
    private String serialNumber;
    /**
    * 参比电压
    */
    @Column(columnDefinition = " varchar(100) COMMENT '参比电压'")
    private String referenceVoltage;
    /**
    * 参比电流
    */
    @Column(columnDefinition = " varchar(100) COMMENT '参比电流'")
    private String referenceCurrent;
    /**
    * 协议通讯地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '协议通讯地址'")
    private String protocolAddress;
    /**
    * 电表常数
    */
    @Column(columnDefinition = " varchar(100) COMMENT '电表常数'")
    private String meterConstant;
    /**
    * 电表准确度
    */
    @Column(columnDefinition = " varchar(100) COMMENT '电表准确度'")
    private String meterAccuracy;
    /**
    * 运维班组
    */
    @Column(columnDefinition = " int(11) COMMENT '运维班组'")
    private Long operationsTeam;

    /**
     * 单/三相:单相：1 三相 2
     */
    @Column(columnDefinition = " int COMMENT '单/三相'")
    private Integer threePhase;

}
