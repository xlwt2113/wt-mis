package com.wt.mis.fi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.FileUtil;
import com.wt.mis.dev.entity.Topology;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "fi_line")
@EqualsAndHashCode(callSuper = true)
public class FiLine extends BaseEntity {

    /**
    * 线路名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '线路名称'")
    private String lineName;

    /**
     * 只用于拓扑图上展示的归属线路的台区设备列表
     */
    @Transient
    private List<FiDevHub> devHubList;

}
