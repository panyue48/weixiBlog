package com.weixi.blog.service.impl;

import com.weixi.blog.entity.Type;
import com.weixi.blog.mapper.TypeMapper;
import com.weixi.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class TypeServiceImpl implements TypeService {
    
    @Autowired
    private TypeMapper typeMapper;
    
    @Override
    public List<Type> getTypesByUserId(Long userId) {
        return typeMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Type> getAllTypes() {
        return typeMapper.selectAll();
    }
    
    @Override
    @Transactional
    public Long createType(Long userId, Type type) {
        type.setUserId(userId);
        typeMapper.insert(type);
        return type.getId();
    }
    
    @Override
    @Transactional
    public void updateType(Long id, Long userId, Type type) {
        Type existingType = typeMapper.selectByIdAndUserId(id, userId);
        if (existingType == null) {
            throw new RuntimeException("分类不存在或无权限");
        }
        type.setId(id);
        type.setUserId(userId);
        typeMapper.updateById(type);
    }
    
    @Override
    @Transactional
    public void deleteType(Long id, Long userId) {
        Type type = typeMapper.selectByIdAndUserId(id, userId);
        if (type == null) {
            throw new RuntimeException("分类不存在或无权限");
        }
        typeMapper.deleteById(id, userId);
    }
}
