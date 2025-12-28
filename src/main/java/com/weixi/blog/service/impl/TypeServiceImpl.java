package com.weixi.blog.service.impl;

import com.weixi.blog.entity.Type;
import com.weixi.blog.mapper.TypeMapper;
import com.weixi.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class TypeServiceImpl implements TypeService {
    
    @Autowired
    private TypeMapper typeMapper;
    
    @Override
    public List<Type> getAllTypes() {
        return typeMapper.selectAll();
    }
}

