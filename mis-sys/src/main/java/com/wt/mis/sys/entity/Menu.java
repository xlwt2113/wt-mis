package com.wt.mis.sys.entity;

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

/**
 * @author 王涛
 */
@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "sys_menu")
public class Menu extends BaseEntity {

    @Column(columnDefinition = " varchar(200) COMMENT '菜单名'")
    private String title;

    @Column(columnDefinition = " varchar(200) COMMENT '链接地址'")
    private String href;

    @Column(columnDefinition = " varchar(200) COMMENT '链接图标'")
    private String icon;

    @Column(columnDefinition = " varchar(200) COMMENT '打开方式'")
    private String target;

    @Column(columnDefinition = " INT COMMENT '菜单类型：0 导航目录 1 目录  2 菜单 3 功能/按钮'")
    private Integer menuType;

    @Column(columnDefinition = " INT COMMENT '父菜单Id'")
    private Long parId;

    @Column(columnDefinition = " int COMMENT '排序号'")
    private int seq;

    /**
     * treeTable控件展开属性
     */
    @Transient
    private boolean open;

    /**
     * tree控件展开属性
     */
    @Transient
    private boolean spread;

    @Transient
    private List<Menu> children;

    public Map<Integer, String> getMenuTypeMap() {
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "导航目录");
        typeMap.put(1, "目录");
        typeMap.put(2, "菜单");
        typeMap.put(3, "功能/按钮");
        return typeMap;
    }

    public String getMenuTypeName() {
        return getMenuTypeMap().get(this.getMenuType());
    }

    public String getIcon() {
        if (this.icon == null) {
            //默认给个图标
            return "fa-th-large";
        } else {
            return this.icon;
        }
    }

    public String getName(){
        return this.getTitle();
    }

}
