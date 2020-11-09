package com.wt.mis.fi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "fi_dev_hub")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class FiDevHub extends BaseEntity {


    /**
    * 终端地址
    */
    @Column(columnDefinition = " int COMMENT '终端地址'")
    private Long hubTermaddr;
    /**
    * 安装位置
    */
    @Column(columnDefinition = " varchar(100) COMMENT '安装位置'")
    private String hubLocation;
    /**
    * 是否在线，0-在线，1-不在线
    */
    @Column(columnDefinition = " int COMMENT '是否在线，0-在线，1-不在线'")
    private int onlineStatus;
    /**
    * 父节点
    */
    @Column(columnDefinition = " int COMMENT '父节点'")
    private Long parentId;
    /**
    * 属性
    */
    @Column( name = "thermometry_1", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry1;
    /**
    * 属性
    */
    @Column( name = "thermometry_2", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry2;
    /**
    * 属性
    */
    @Column( name = "thermometry_3", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry3;
    /**
    * 属性
    */
    @Column( name = "thermometry_4", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry4;
    /**
    * 属性
    */
    @Column( name = "thermometry_5", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry5;
    /**
    * 属性
    */
    @Column( name = "thermometry_6", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry6;
    /**
    * 属性
    */
    @Column( name = "thermometry_7", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry7;
    /**
    * 属性
    */
    @Column( name = "thermometry_8", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry8;
    /**
    * 属性
    */
    @Column( name = "thermometry_9", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry9;
    /**
    * 属性
    */
    @Column( name = "thermometry_10", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry10;
    /**
    * 属性
    */
    @Column( name = "thermometry_11", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry11;
    /**
    * 属性
    */
    @Column( name = "thermometry_12", columnDefinition = " varchar(100) COMMENT '属性'")
    private String thermometry12;
    /**
    * 归属线路
    */
    @Column(columnDefinition = " int COMMENT '归属线路'")
    private Long lineId;

    @Column(columnDefinition = " float(11,2) COMMENT 'x坐标'")
    private Float posX;

    @Column(columnDefinition = " float(11,2) COMMENT 'y坐标'")
    private Float posY;

    /**
     * 归属线路
     */
    @Column(columnDefinition = " int default 0 COMMENT '节点类型 0 设备  1 虚拟'")
    private Integer nodeType;

    public String getOnlineStatusName(){
        return this.onlineStatus==0?"在线":"不在线";
    }

}
