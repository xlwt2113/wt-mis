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
@Table(name = "fi_yx_history")
@EqualsAndHashCode(callSuper = true)
public class YxHistory extends BaseEntity {

    /**
    * 汇集单元id
    */
    @Column(columnDefinition = " int COMMENT '汇集单元id'")
    private Integer hubId;
    /**
    * 信息体地址
    */
    @Column(columnDefinition = " int COMMENT '信息体地址'")
    private Integer inforAddr;
    /**
    * 遥信值 0 分 1 合
    */
    @Column(columnDefinition = " int COMMENT '遥信值 0 分 1 合'")
    private Integer yxValue;

}
