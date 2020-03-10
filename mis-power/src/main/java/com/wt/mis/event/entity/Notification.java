package com.wt.mis.event.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "event_notification")
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    /**
    * 设备ID
    */
    @Column(columnDefinition = " int COMMENT '设备ID'")
    private Long devId;
    /**
    * 设备类型
    */
    @Column(columnDefinition = " int COMMENT '设备类型'")
    private Integer devType;
    /**
    * 事件类型
    */
    @Column(columnDefinition = " int COMMENT '事件类型'")
    private Integer eventType;
    /**
    * 事件状态
    */
    @Column(columnDefinition = " int COMMENT '事件状态'")
    private Integer eventStatus;
    /**
    * 事件返回值
    */
    @Column(columnDefinition = " varchar(100) COMMENT '事件返回值'")
    private String eventValue;
    /**
    * 事件接收处理方
    */
    @Column(columnDefinition = " int COMMENT '事件接收处理方'")
    private Integer eventReceiver;
    /**
    * 事件处理优先级
    */
    @Column(columnDefinition = " int COMMENT '事件处理优先级'")
    private Integer eventPriority;

}
