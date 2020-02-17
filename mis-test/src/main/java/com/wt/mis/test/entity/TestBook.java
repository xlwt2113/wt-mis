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
@Table(name = "test_book")
@EqualsAndHashCode(callSuper = true)
public class TestBook extends BaseEntity {

    @Column(columnDefinition = " varchar(20) COMMENT '账号名'")
    private String bookName;

    @Column(columnDefinition = " varchar(20) COMMENT '账号名'")
    private String bookType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(columnDefinition = " date COMMENT '账号名'")
    private Date beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = " datetime COMMENT '账号名'")
    private Date endDate;

    @Column(columnDefinition = " int COMMENT '账号名'")
    private Integer flag;

    @Column(columnDefinition = " varchar(200) COMMENT '账号名'")
    private String note;

    @Column(columnDefinition = " varchar(2000) COMMENT 'fileId上传附件'")
    private String fileId;

    /**
     * 获取附件html显示
     */
    public String getFileIdHtml(){
        return FileUtil.getFilesHtml(this.getFileId(),"fileId");
    }

}
