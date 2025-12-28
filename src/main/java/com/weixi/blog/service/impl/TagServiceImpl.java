package com.weixi.blog.service.impl;

import com.weixi.blog.entity.Tag;
import com.weixi.blog.mapper.TagMapper;
import com.weixi.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Override
    public List<Tag> getTagsByUserId(Long userId) {
        return tagMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }
    
    @Override
    @Transactional
    public Long createTag(Long userId, Tag tag) {
        tag.setUserId(userId);
        tagMapper.insert(tag);
        return tag.getId();
    }
    
    @Override
    @Transactional
    public void updateTag(Long id, Long userId, Tag tag) {
        Tag existingTag = tagMapper.selectByIdAndUserId(id, userId);
        if (existingTag == null) {
            throw new RuntimeException("标签不存在或无权限");
        }
        tag.setId(id);
        tag.setUserId(userId);
        tagMapper.updateById(tag);
    }
    
    @Override
    @Transactional
    public void deleteTag(Long id, Long userId) {
        Tag tag = tagMapper.selectByIdAndUserId(id, userId);
        if (tag == null) {
            throw new RuntimeException("标签不存在或无权限");
        }
        tagMapper.deleteById(id, userId);
    }
}
