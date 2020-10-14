package com.wt.mis.fi.entity;

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
@Table(name = "fi_day_sms")
@EqualsAndHashCode(callSuper = true)
public class DaySms extends BaseEntity {

    /**
    * 用户id
    */
    @Column(columnDefinition = " int COMMENT '用户id'")
    private Integer userId;
    /**
    * 用户所选设备id，逗号分隔
    */
    @Column(columnDefinition = " varchar(100) COMMENT '用户所选设备id，逗号分隔'")
    private String devIds;
    /**
    * 发送时间
    */
    @Column(columnDefinition = " varchar(100) COMMENT '发送时间'")
    private String sendTime;
    /**
    * 用户所选的的测点，逗号分隔
    */
    @Column(columnDefinition = " int COMMENT '用户所选的的测点，逗号分隔'")
    private String pointTypes;
    /**
    * 是否接收故障通知短信
    */
    @Column(columnDefinition = " int COMMENT '是否接收故障通知短信'")
    private Integer receiveGzInfo;

}
