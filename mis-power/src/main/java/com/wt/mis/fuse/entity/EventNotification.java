package com.wt.mis.fuse.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "fuse_event_notification")
@EqualsAndHashCode(callSuper = true)
public class EventNotification extends BaseEntity {

    /**
    * 汇集单元ID
    */
    @Column(columnDefinition = " int COMMENT '汇集单元ID'")
    private Long hubId;
    /**
    * 熔断器事件类型
    */
    @Column(columnDefinition = " int COMMENT '熔断器事件类型'")
    private Integer eventType;

    /**
     * 熔断器事件类型
     */
    @Column(columnDefinition = " int COMMENT '熔断器事件状态'")
    private Integer eventStatus;

    /**
     * 熔断器事件类型
     */
    @Column(columnDefinition = " varchar(500)  COMMENT '熔断器事件值'")
    private String eventValue;

    /**
     * 熔断器事件类型
     */
    @Column(columnDefinition = " int COMMENT '熔断器事件接收方'")
    private Integer eventReceiver;


}
