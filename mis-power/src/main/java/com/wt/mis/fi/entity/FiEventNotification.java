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
@Table(name = "fi_event_notification")
@EqualsAndHashCode(callSuper = true)
public class FiEventNotification extends BaseEntity {

    /**
    * 汇集单元ID
    */
    @Column(columnDefinition = " int COMMENT '汇集单元ID'")
    private Long hubId;
    /**
    * 事件类型
    */
    @Column(columnDefinition = " int COMMENT '事件类型'")
    private Integer eventType;
    /**
    * 事件状态 0：未启动 1：进行中 2：成功 3：失败
    */
    @Column(columnDefinition = " int COMMENT '事件状态 0：未启动 1：进行中 2：成功 3：失败'")
    private int eventStatus;
    /**
    * 事件附加值
    */
    @Column(columnDefinition = " varchar(100) COMMENT '事件附加值'")
    private String eventValue;
    /**
    * 事件接收方 1：主站 2：前置机
    */
    @Column(columnDefinition = " int COMMENT '事件接收方 1：主站 2：前置机'")
    private int eventReceiver;

}
