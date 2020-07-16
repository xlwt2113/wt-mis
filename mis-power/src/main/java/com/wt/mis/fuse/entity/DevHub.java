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
@Table(name = "fuse_dev_hub")
@EqualsAndHashCode(callSuper = true)
public class DevHub extends BaseEntity {

    /**
    * 通信地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT '通信地址'")
    private String hubAddress;
    /**
    * 名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT '安装位置'")
    private String hubLocation;
    /**
    * 是否在线
    */
    @Column(columnDefinition = " int COMMENT '是否在线'")
    private int onlineStatus;
    /**
    * A相熔断器地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'A相熔断器地址'",name = "fuse_a_address")
    private String fuseAaddress;
    /**
    * A相熔断器名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'A相熔断器名称'",name = "fuse_a_name")
    private String fuseAname;
    /**
    * B相熔断器地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'B相熔断器地址'",name = "fuse_b_address")
    private String fuseBaddress;
    /**
    * B相熔断器名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'B相熔断器名称'",name = "fuse_b_name")
    private String fuseBname;
    /**
    * C相熔断器地址
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'C相熔断器地址'",name = "fuse_c_address")
    private String fuseCaddress;
    /**
    * C相熔断器名称
    */
    @Column(columnDefinition = " varchar(100) COMMENT 'C相熔断器名称'",name = "fuse_c_name")
    private String fuseCname;

}
