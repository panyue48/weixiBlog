package com.weixi.blog.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> {
    private List<T> records;  // 数据列表
    private Long total;        // 总记录数
    private Integer current;   // 当前页
    private Integer size;      // 每页大小
    private Integer pages;     // 总页数
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }
}

