package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_dict_item")
public class DictItem extends BaseEntity {

    @Column(columnDefinition = " varchar(200) COMMENT '键名'")
    private String itemKey;

    @Column(columnDefinition = " varchar(200) COMMENT '键值'")
    private String itemValue;

    @Column(columnDefinition = " int(2) COMMENT '排序'")
    private int seq;

    //json忽略，否则调用json格式数据时会变成循环引用
    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "dict_id")
    private Dict dict;
}
