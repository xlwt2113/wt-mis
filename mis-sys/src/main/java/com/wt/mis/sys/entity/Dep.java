package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.mis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "sys_dep")
public class Dep extends BaseEntity {


    @Column(columnDefinition = " varchar(20) COMMENT '机构名称'")
    private String name;

    @Column(columnDefinition = " int COMMENT '机构类型'")
    private Integer depType;

    @Column(columnDefinition = " int COMMENT '上级机构ID'")
    private Long pid;

    @Column(columnDefinition = " int COMMENT '排序号'")
    private int seq;

    @Column(columnDefinition = " varchar(20) COMMENT '机构层级,用于查询，由父id加下划线组成'")
    private String level;

    @JsonIgnore
    public Map<Integer, String> getDepTypeMap() {
        Map<Integer, String> typeMap = new HashMap<>();
//        typeMap.put(0,"部");
        typeMap.put(1, "集团");
        typeMap.put(2, "公司");
        typeMap.put(3, "部门");
//        typeMap.put(4,"车间");
//        typeMap.put(5,"工区");
        return typeMap;
    }

    public String getDepTypeName() {
        return this.getDepTypeMap().get(this.getDepType());
    }


    @Transient
    private boolean open;
    /**
     * 下级机构
     */
    @Transient
    private List<Dep> children;


}
