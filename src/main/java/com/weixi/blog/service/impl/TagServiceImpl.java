package com.weixi.blog.service.impl;

import com.weixi.blog.entity.Tag;
import com.weixi.blog.mapper.TagMapper;
import com.weixi.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }
}

