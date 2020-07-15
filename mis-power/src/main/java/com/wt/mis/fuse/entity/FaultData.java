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
@Table(name = "fuse_fault_data")
@EqualsAndHashCode(callSuper = true)
public class FaultData extends BaseEntity {

    /**
    * 汇集单元
    */
    @Column(columnDefinition = " varchar(100) COMMENT '汇集单元'")
    private String hubId;
    /**
    * 熔断器事件类型
    */
    @Column(columnDefinition = " int COMMENT '熔断器事件类型'")
    private Integer eventType;
    /**
    * 状态/接收数据组数
    */
    @Column(columnDefinition = " int COMMENT '状态/接收数据组数'",name = "data_1")
    private Integer data1;
    /**
    * 异常点前数据采样间隔(秒)
    */
    @Column(columnDefinition = " int COMMENT '异常点前数据采样间隔(秒)'",name = "data_2")
    private Integer data2;
    /**
    * 异常点（含）数据采样间隔(秒)
    */
    @Column(columnDefinition = " int COMMENT '异常点（含）数据采样间隔(秒)'",name = "data_3")
    private Integer data3;
    /**
    * 当前组别
    */
    @Column(columnDefinition = " int COMMENT '当前组别'",name = "data_4")
    private Integer data4;
    /**
    * CT是否接入
    */
    @Column(columnDefinition = " double COMMENT 'CT是否接入'",name = "data_5")
    private Double data5;
    /**
    * 取电电压(V)
    */
    @Column(columnDefinition = " double COMMENT '取电电压(V)'",name = "data_6")
    private Double data6;
    /**
    * 超级电容电压(V)
    */
    @Column(columnDefinition = " double COMMENT '超级电容电压(V)'",name = "data_7")
    private Double data7;
    /**
    * 电池电压(V)
    */
    @Column(columnDefinition = " double COMMENT '电池电压(V)'",name = "data_8")
    private Double data8;
    /**
    * 电场值
    */
    @Column(columnDefinition = " int COMMENT '电场值'",name = "data_9")
    private Integer data9;
    /**
    * 线路电流(A)
    */
    @Column(columnDefinition = " double COMMENT '线路电流(A)'",name = "data_10")
    private Double data10;

}
