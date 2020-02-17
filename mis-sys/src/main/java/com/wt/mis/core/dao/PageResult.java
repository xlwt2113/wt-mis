package com.wt.mis.core.dao;

import lombok.Data;

import java.util.List;

/**
 * @author wt
 * 查询数据后的分页信息
 */
@Data
public class PageResult {
    /**
     * 数据集
     */
    private List content;
    /**
     * 当前页数
     */
    private long pageNumber;
    /**
     * 每页显示数据条数
     */
    private long pageSize;
    /**
     * 总条数
     */
    private long totalElements;
}
