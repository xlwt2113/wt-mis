package com.wt.mis.dev.entity;

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
@Table(name = "dev_meter_box")
@EqualsAndHashCode(callSuper = true)
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
    @Column(columnDefinition = " varchar(100) COMMENT '运维班组'")
    private String operationsTeam;

}
