package com.wt.mis.sys.entity;

import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "sys_dict")
public class Dict extends BaseEntity {

    @Column(columnDefinition = " varchar(200) COMMENT '字典名称'")
    private String dictName;

    @Column(columnDefinition = " varchar(200) COMMENT '字典编码'")
    private String dictCode;

    @OneToMany(mappedBy = "dict", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DictItem> dictItemList;

    public List<DictItem> getDictItemList() {
        return dictItemList;
    }

    public String getDictItems() {
        StringBuffer sb = new StringBuffer();
        dictItemList.sort(new Comparator<DictItem>() {
            @Override
            public int compare(DictItem o1, DictItem o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        for (DictItem item : dictItemList) {
            sb.append(item.getItemKey() + " : " + item.getItemValue() + " ; ");
        }
        if (!StringUtils.isEmpty(sb.toString())) {
            return sb.toString().substring(0, sb.toString().length() - 2);
        } else {
            return "";
        }
    }


}
