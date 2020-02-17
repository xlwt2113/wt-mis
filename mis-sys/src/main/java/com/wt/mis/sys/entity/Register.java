package com.wt.mis.sys.entity;

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
@Table(name = "sys_register")
@EqualsAndHashCode(callSuper = true)
public class Register extends BaseEntity {

    /**
    * 注册项目名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '注册项目名称'")
    private String itemName;
    /**
    * 注册项目值
    */
    @Column(columnDefinition = " varchar(100) COMMENT '注册项目值'")
    private String itemValue;
    /**
    * 注册项说明
    */
    @Column(columnDefinition = " varchar(100) COMMENT '注册项说明'")
    private String comment;

}
