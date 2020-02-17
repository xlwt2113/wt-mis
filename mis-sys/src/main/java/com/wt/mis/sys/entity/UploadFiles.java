package com.wt.mis.sys.entity;

import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Slf4j
@Data
@Entity
@Table(name = "sys_uploads")
@EqualsAndHashCode(callSuper = true)
public class UploadFiles extends BaseEntity {

    @Column(columnDefinition = " varchar(200) COMMENT '新名称'") //手动制定列名注释，字段大小，
    private String fileName;

    @Column(columnDefinition = " varchar(200) COMMENT '原始文件名'") //手动制定列名注释，字段大小，
    private String sourceName;

    @Column(columnDefinition = " varchar(200) COMMENT '文件路径'") //手动制定列名注释，字段大小，
    private String filePath;

}
