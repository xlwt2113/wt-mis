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
@Table(name = "data_freeze")
@EqualsAndHashCode(callSuper = true)
public class Freeze extends BaseEntity {

    /**
    * 设备ID
    */
    @Column(columnDefinition = " int COMMENT '设备ID'")
    private int devId;
    /**
    * 设备类型
    */
    @Column(columnDefinition = " int COMMENT '设备类型'")
    private Integer devType;
    /**
    * 冻结数据类型
    */
    @Column(columnDefinition = " int COMMENT '冻结数据类型'")
    private Integer dataType;
    /**
    * 召测时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '召测时间'")
    private Date callTime;
    /**
    * 冻结时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '冻结时间'")
    private Date freezeTime;
    /**
    * 冻结数据
    */
    @Column(columnDefinition = " double COMMENT '冻结数据'")
    private double freezeData;

}