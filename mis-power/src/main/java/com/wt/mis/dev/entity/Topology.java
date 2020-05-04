package com.wt.mis.dev.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@Entity
@Table(name = "dev_topology")
@EqualsAndHashCode(callSuper = true)
@Proxy(lazy = false)
public class Topology extends BaseEntity {

    /**
    * 台区（变压器）ID
    */
    @Column(columnDefinition = " int COMMENT '台区（变压器）ID'")
    private Long transformId;
    /**
    * 设备ID
    */
    @Column(columnDefinition = " int(11) COMMENT '设备ID'")
    private Long devId;
    /**
    * 设备类型
    */
    @Column(columnDefinition = " int COMMENT '设备类型'")
    private Integer devType;
    /**
    * 设备显示名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备显示名称'")
    private String devName;
    /**
    * 设备通讯地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备通讯地址'")
    private String devAddress;
    /**
    * 设备在线状态
    */
    @Column(columnDefinition = " int COMMENT '设备在线状态'")
    private Integer devOnline;
    /**
    * 设备在汇聚单元中序号
    */
    @Column(columnDefinition = " int COMMENT '设备在汇聚单元中序号'")
    private Integer devNum;
    /**
    * 设备测量点号
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备测量点号'")
    private String devMeasureNum;
    /**
    * 设备相位
    */
    @Column(columnDefinition = " varchar(100) COMMENT '设备相位'")
    private String devPhase;
    /**
    * 父节点设备ID
    */
    @Column(columnDefinition = " varchar(100) COMMENT '父节点设备ID'")
    private String devParentId;
    /**
    * 父节点设备类型
    */
    @Column(columnDefinition = " int COMMENT '父节点设备类型'")
    private Integer devParentType;
    /**
    * 父节点设备显示名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '父节点设备显示名称'")
    private String devParentName;
    /**
    * 父节点设备通讯地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '父节点设备通讯地址'")
    private String devParentAddress;
    /**
    * 设备是否存在
    */
    @Column(columnDefinition = " int COMMENT '设备是否存在'")
    private Integer devExist;

    /**
     * 只用于拓扑图上展示的归属线路的台区设备列表
     */
    @Transient
    private List<Topology> transfromList;

}
