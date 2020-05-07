package com.wt.mis.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Entity
@Table(name = "transform_time_task")
@EqualsAndHashCode(callSuper = true)
public class TimeTask extends BaseEntity {

    /**
    * 任务类型
    */
    @Column(columnDefinition = " int COMMENT '任务类型'")
    private Integer taskType;
    /**
    * 台区Id
    */
    @Column(columnDefinition = " int COMMENT '台区Id'")
    private Long transformId;
    /**
    * 时间间隔类型
    */
    @Column(columnDefinition = " int COMMENT '时间间隔类型'")
    private int intervalType;
    /**
    * 执行日
    */
    @Column(columnDefinition = " int COMMENT '执行日'")
    private int taskDay;
    /**
    * 任务执行时刻
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '任务执行时刻'")
    private Date taskTime;

    @Transient
    private String taskTimeStr;
    /**
    * 下次任务执行时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '下次任务执行时间'")
    private Date nextTask;
    /**
    * 定时任务执行状态
    */
    @Column(columnDefinition = " int COMMENT '定时任务执行状态'")
    private Integer taskState;
    /**
    * 归属部门
    */
    @Column(columnDefinition = " int COMMENT '归属部门'")
    private Long depId;



}
