package com.wt.mis.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_code_info_item")
public class CodeInfoItem extends BaseEntity {

    @Column(columnDefinition = " varchar(1000) COMMENT 'po列名称'")
    private String poColName;

    @Column(columnDefinition = " varchar(1000) COMMENT 'po列属性'")
    private String poColProperty;

    @Column(columnDefinition = " varchar(1000) COMMENT '列名备注'")
    private String poColNote;

    @Column(columnDefinition = " varchar(10) COMMENT '是否为查询条件'")
    private String conditions;

    @Column(columnDefinition = " varchar(10) COMMENT '是否为要显示的列名'")
    private String showColName;

    @Column(columnDefinition = " varchar(10) COMMENT '是否必填:是，否'")
    private String required;

    @Column(columnDefinition = " varchar(100) COMMENT '控件类型'")
    private String inputType;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "code_id")
    private CodeInfo codeInfo;


    public String getUpperCasePoColName(){
        return StringUtils.toUpperCaseFirstOne(this.getPoColName());
    }


    /**
     * 获取数据库的列名
     * @return
     */
    public String getDbColName(){
        return StringUtils.toDbName(this.getPoColName());
    }

    /**
     * 将java对象转为数据库对象
     * @return
     */
    public String getDbColProperty(){
        switch(this.getPoColProperty()){
            case "String":
                return "varchar";
            case "Integer" :
                return "int";
            case "int":
                return "int";
            case "Float":
                return "float";
            case "float":
                return "float";
            case "double":
                return "double";
            case "Double":
                return "double";
            case "Date":
                return "datetime";
            default:
                return "varchar";
        }
    }

}
