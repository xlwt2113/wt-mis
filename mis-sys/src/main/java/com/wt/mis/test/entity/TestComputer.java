package com.wt.mis.test.entity;

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
@Table(name = "test_computer")
@EqualsAndHashCode(callSuper = true)
public class TestComputer extends BaseEntity {

    /**
    * CPU
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'CPU'")
    private String cpu;
    /**
    * 内存
    */
    @Column(columnDefinition = " varchar(100) COMMENT '内存'")
    private String memery;
    /**
    * 硬盘
    */
    @Column(columnDefinition = " varchar(100) COMMENT '硬盘'")
    private String disk;
    /**
    * 生产日期
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(columnDefinition = " date COMMENT '生产日期'")
    private Date product;
    /**
    * USB个数
    */
    @Column(columnDefinition = " int COMMENT 'USB个数'")
    private Integer usb;
    /**
    * 备注
    */
    @Column(columnDefinition = " varchar(100) COMMENT '备注'")
    private String note;
    /**
    * 图片
    */
    @Column(columnDefinition = " varchar(2000) COMMENT '图片'")
    private String img;
    /**
    * 获取附件图片html显示
    */
    public String getImgHtml(){
        return FileUtil.getFilesHtml(this.getImg(),"img");
    }
    /**
    * 压缩
    */
    @Column(columnDefinition = " varchar(2000) COMMENT '压缩'")
    private String zip;
    /**
    * 获取附件压缩html显示
    */
    public String getZipHtml(){
        return FileUtil.getFilesHtml(this.getZip(),"zip");
    }

}
