package com.wt.mis.sms.entity;

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
@Table(name = "sms_mobile")
@EqualsAndHashCode(callSuper = true)
public class Mobile extends BaseEntity {

    /**
    * 手机号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '手机号'")
    private String mobile;
    /**
    * 姓名
    */
    @Column(columnDefinition = " varchar(100) COMMENT '姓名'")
    private String name;
    /**
    * 人员信息说明
    */
    @Column(columnDefinition = " varchar(100) COMMENT '人员信息说明'")
    private String note;
    /**
    * 短信接收人员分组
    */
    @Column(columnDefinition = " varchar(100) COMMENT '短信接收人员分组'")
    private String groupName;

}
