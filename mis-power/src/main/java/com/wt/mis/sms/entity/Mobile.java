package com.wt.mis.sms.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "sms_mobile")
@EqualsAndHashCode(callSuper = true)
public class Mobile extends BaseEntity {

    /**
    * 手机号
    */
    @Column(columnDefinition = " int(11) COMMENT '账号ID'")
    private long accountId;
    /**
    * 姓名
    */
    @Column(columnDefinition = " int(11) COMMENT '台区ID'")
    private long transformId;

    @Transient
    private String mobile;

    @Transient
    private String name;

}
