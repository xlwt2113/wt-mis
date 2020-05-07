package com.wt.mis.dev.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Entity
@Table(name = "transform_dev_transform")
@EqualsAndHashCode(callSuper = true)
@Proxy(lazy = false)
public class TransForm extends BaseEntity {

    /**
    * 台区名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '台区名称'")
    private String transformName;
    /**
    * 台区编号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '台区编号'")
    private String transformNum;
    /**
    * 设备安装位置
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备安装位置'")
    private String installationLocation;
    /**
    * 汇聚单元地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '汇聚单元地址'")
    private String devAddress;
    /**
    * 协议通讯地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '协议通讯地址'")
    private String protocolAddress;
    /**
    * 变压器制造商
    */
    @Column(columnDefinition = " varchar(100) COMMENT '变压器制造商'")
    private String transformFactory;
    /**
    * 出厂编号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '出厂编号'")
    private String serialNumber;
    /**
    * 出厂日期
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(columnDefinition = " date COMMENT '出厂日期'")
    private Date manufacturingDate;
    /**
    * 运维班组
    */
    @Column(columnDefinition = " int(11) COMMENT '运维班组编号'")
    private Long operationsTeam;

    /**
     * 运维班组
     */
    @Column(columnDefinition = " int(11) COMMENT '台区归属线路ID'")
    private Long lineId;

    /**
     * 注册设备数量
     */
    @Column(columnDefinition = " int(11) default 0 COMMENT '注册设备数'")
    private int devNum;

    @Transient
    private  String lineName;

}
