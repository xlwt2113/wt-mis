package com.wt.mis.fuse.entity;

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
@Table(name = "fuse_real_data")
@EqualsAndHashCode(callSuper = true)
public class RealData extends BaseEntity {

    /**
    * 汇集单元ID
    */
    @Column(columnDefinition = " int COMMENT '汇集单元ID'")
    private Integer hubId;
    /**
    * 熔断器事件类型
    */
    @Column(columnDefinition = " varchar(100) COMMENT '熔断器事件类型'")
    private String eventType;
    /**
    * CT是否接入 0未接入 1接入
    */
    @Column(columnDefinition = " int COMMENT 'CT是否接入 0未接入 1接入'",name = "data_1")
    private int data1;
    /**
    * 取电电压(V)
    */
    @Column(columnDefinition = " double COMMENT '取电电压(V)'",name = "data_2")
    private Double data2;
    /**
    * 超级电容电压(V)
    */
    @Column(columnDefinition = " double COMMENT '超级电容电压(V)'",name = "data_3")
    private Double data3;
    /**
    * 电池电压(V)
    */
    @Column(columnDefinition = " double COMMENT '电池电压(V)'",name = "data_4")
    private Double data4;
    /**
    * 线路电压(kV)
    */
    @Column(columnDefinition = " double COMMENT '线路电压(kV)'",name = "data_5")
    private Double data5;
    /**
    * 线路电流(A)
    */
    @Column(columnDefinition = " double COMMENT '线路电流(A)'",name = "data_6")
    private Double data6;

}
