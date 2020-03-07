package com.wt.mis.event.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "event_power_outage")
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
    @Column(columnDefinition = " varchar(100) COMMENT '事件发生时间'")
    private String occurTime;
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
    @Column(name = "voltage_status_a", columnDefinition = " int COMMENT 'A相电压状态'")
    private Integer voltageStatusA;
    /**
    * B相电压状态
    */
    @Column(name = "voltage_status_b",columnDefinition = " int COMMENT 'B相电压状态'")
    private Integer voltageStatusB;
    /**
    * C相电压状态
    */
    @Column(name = "voltage_status_c",columnDefinition = " int COMMENT 'C相电压状态'")
    private Integer voltageStatusC;
    /**
     * 是否是历史记录
     */
    @Column(columnDefinition = " int COMMENT '是否是历史记录，0为最新记录，1为历史记录'")
    private int history;

}
