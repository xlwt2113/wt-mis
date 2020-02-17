package com.wt.mis.core.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author 王涛
 * 抽象基类，所有实例化的类都继承该类
 */
@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    /**
     * 设置自增字段
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 创建时间
     */
    @Column
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column
    protected LocalDateTime updateTime;

    /**
     * 删除标记，默认为0未删除 1 已删除
     */
    @Column
    protected int del;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.del = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
