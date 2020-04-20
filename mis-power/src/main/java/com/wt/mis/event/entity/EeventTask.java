package com.wt.mis.event.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "event_task")
@EqualsAndHashCode(callSuper = true)
public class EeventTask extends BaseEntity {

    /**
    * 消息通知接收人ID
    */
    @Column(columnDefinition = " int COMMENT '消息通知接收人ID'")
    private long accountId;
    /**
    * 消息通知内容
    */
    @Column(columnDefinition = " varchar(1000) COMMENT '消息通知内容'")
    private String msg;
    /**
    * 消息详情的查看地址
    */
    @Column(columnDefinition = " varchar(1000) COMMENT '消息详情的查看地址'")
    private String url;

}
