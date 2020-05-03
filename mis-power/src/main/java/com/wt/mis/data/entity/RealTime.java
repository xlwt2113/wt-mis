package com.wt.mis.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "data_real_time_1")
@EqualsAndHashCode(callSuper = true)
public class RealTime extends BaseEntity {

    /**
    * 设备ID
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备ID'")
    private String devId;
    /**
    * 设备类型
    */
    @Column(columnDefinition = " int COMMENT '设备类型'")
    private Integer devType;
    /**
    * 召测时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '召测时间'")
    private Date callTime;
    /**
    * 数据类型
    */
    @Column(columnDefinition = " int COMMENT '数据类型'")
    private Integer dataType;
    /**
    * 召测数据
    */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData1;

    /**
     * 召测数据
     */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData2;

    /**
     * 召测数据
     */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData3;

    /**
     * 召测数据
     */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData4;

    /**
     * 召测数据
     */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData5;

    /**
     * 召测数据
     */
    @Column(columnDefinition = " double COMMENT '召测数据'")
    private double callData6;

}
