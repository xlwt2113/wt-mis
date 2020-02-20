package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_trans_form")
@EqualsAndHashCode(callSuper = true)
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
    * 汇集单元地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '汇集单元地址'")
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
    @Column(columnDefinition = " varchar(100) COMMENT '出厂日期'")
    private String manufacturingDate;
    /**
    * 运维班组
    */
    @Column(columnDefinition = " varchar(100) COMMENT '运维班组'")
    private String operationsTeam;

}
