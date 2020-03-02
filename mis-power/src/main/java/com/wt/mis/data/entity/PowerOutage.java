package com.wt.mis.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "data_power_outage")
@EqualsAndHashCode(callSuper = true)
public class PowerOutage extends BaseEntity {

    /**
    * 设备ID
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备ID'")
    private String devId;
    /**
    * 设备类型
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备类型'")
    private String devType;
    /**
    * 事件发生时间
    */
    @Column(columnDefinition = " datetime COMMENT '事件发生时间'")
    private LocalDateTime occurTime;
    /**
    * 停电状态
    */
    @Column(columnDefinition = " int COMMENT '停电状态'")
    private Integer powerStatus;
    /**
    * 相序状态
    */
    @Column(columnDefinition = " int COMMENT '相序状态'")
    private Integer phaseStatus;
    /**
    * A相电压状态
    */
    @Column(columnDefinition = " int COMMENT 'A相电压状态'")
    private Integer voltageStatusA;
    /**
    * B相电压状态
    */
    @Column(columnDefinition = " int COMMENT 'B相电压状态'")
    private Integer voltageStatusB;
    /**
    * C相电压状态
    */
    @Column(columnDefinition = " int COMMENT 'C相电压状态'")
    private Integer voltageStatusC;

}
