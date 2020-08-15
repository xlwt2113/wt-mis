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
@Table(name = "fi_line")
@EqualsAndHashCode(callSuper = true)
public class FiLine extends BaseEntity {

    /**
    * 线路名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '线路名称'")
    private String lineName;

}
