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
@Table(name = "fi_yc_history")
@EqualsAndHashCode(callSuper = true)
public class YcHistory extends BaseEntity {

    /**
    * 汇集单元ID
    */
    @Column(columnDefinition = " int COMMENT '汇集单元ID'")
    private Integer hubId;
    /**
    * 信息体地址
    */
    @Column(columnDefinition = " int COMMENT '信息体地址'")
    private Integer inforAddr;
    /**
    * 遥测值
    */
    @Column(columnDefinition = " double COMMENT '遥测值'")
    private Double ycValue;

}
