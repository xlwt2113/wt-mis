package com.wt.mis.sys.entity;

import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "sys_code_info")
public class CodeInfo extends BaseEntity {

    /**
     * 访问路径
     */
    @Column(columnDefinition = " varchar(30) COMMENT '访问路径'")
    private String url;
    /**
     * 所在包名
     */
    @Column(columnDefinition = " varchar(100) COMMENT '所在包名'")
    private String packageName;

    /**
     * 对应表名
     */
    @Column(columnDefinition = " varchar(100) COMMENT '对应po类名'")
    private String poName;

    /**
     * 归属上级菜单
     */
    @Column(columnDefinition = " int COMMENT '上级菜单目录ID'")
    private Long  menuId;


    /**
     * 菜单名称
     */
    @Column(columnDefinition = " varchar(100) COMMENT '菜单名称'")
    private String  title;



    @OneToMany(mappedBy = "codeInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CodeInfoItem> codeInfoItemList;

    public String getTableName(){
        return StringUtils.toDbName(this.getPoName());
    }

    /**
     * 获取首字母为小写的po类名
     * @return
     */
    public String getLowerCaseFirstPoName(){
        return StringUtils.toLowerCaseFirstOne(this.getPoName());
    }

    /**
     * 生成访问模板的路径
     * @return
     */
    public String getPrefixUrl(){
        return url.substring(1,url.length());
    }

}
