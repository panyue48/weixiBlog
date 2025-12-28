package com.weixi.blog.service.impl;

import com.weixi.blog.common.PageResult;
import com.weixi.blog.dto.BlogDTO;
import com.weixi.blog.entity.Blog;
import com.weixi.blog.mapper.BlogMapper;
import com.weixi.blog.mapper.BlogTagsMapper;
import com.weixi.blog.service.BlogService;
import com.weixi.blog.vo.BlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客服务实现类
 */
@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
    
    @Autowired
    private BlogMapper blogMapper;
    
    @Autowired
    private BlogTagsMapper blogTagsMapper;
    
    @Override
    public PageResult<BlogVO> getBlogPage(Integer current, Integer size, String keyword, Long typeId, Long tagId, Integer published) {
        // 计算偏移量
        Integer offset = (current - 1) * size;
        
        // 查询数据
        List<BlogVO> records = blogMapper.selectBlogPage(keyword, typeId, tagId, published, offset, size);
        
        // 查询总数
        Long total = (long) blogMapper.countBlogs(keyword, typeId, tagId, published);
        
        // 去重标签（因为LEFT JOIN可能产生重复）
        records.forEach(blogVO -> {
            if (blogVO.getTags() != null) {
                List<com.weixi.blog.vo.TagVO> distinctTags = blogVO.getTags().stream()
                    .collect(Collectors.toMap(
                        com.weixi.blog.vo.TagVO::getId,
                        tag -> tag,
                        (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
                blogVO.setTags(distinctTags);
            }
        });
        
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public BlogVO getBlogDetail(Long id) {
        BlogVO blogVO = blogMapper.selectBlogDetailById(id);
        if (blogVO != null) {
            // 去重标签（因为LEFT JOIN可能产生重复）
            if (blogVO.getTags() != null) {
                List<com.weixi.blog.vo.TagVO> distinctTags = blogVO.getTags().stream()
                    .collect(Collectors.toMap(
                        com.weixi.blog.vo.TagVO::getId,
                        tag -> tag,
                        (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
                blogVO.setTags(distinctTags);
            }
        }
        return blogVO;
    }
    
    @Override
    @Transactional
    public Long saveBlog(BlogDTO blogDTO, Long userId) {
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogDTO, blog);
        blog.setUserId(userId);
        blog.setViews(0);
        if (blog.getPublished() == null) {
            blog.setPublished(1); // 默认发布
        }
        
        blogMapper.insert(blog);
        
        // 保存标签关联
        if (blogDTO.getTagIds() != null && !blogDTO.getTagIds().isEmpty()) {
            blogTagsMapper.insertBatch(blog.getId(), blogDTO.getTagIds());
        }
        
        return blog.getId();
    }
    
    @Override
    @Transactional
    public void updateBlog(Long id, BlogDTO blogDTO, Long userId) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此博客");
        }
        
        BeanUtils.copyProperties(blogDTO, blog);
        blog.setId(id);
        blogMapper.updateById(blog);
        
        // 删除旧标签关联
        blogTagsMapper.deleteByBlogId(id);
        
        // 保存新标签关联
        if (blogDTO.getTagIds() != null && !blogDTO.getTagIds().isEmpty()) {
            blogTagsMapper.insertBatch(id, blogDTO.getTagIds());
        }
    }
    
    @Override
    @Transactional
    public void deleteBlog(Long id, Long userId) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此博客");
        }
        
        // 删除标签关联（外键级联删除会自动处理）
        blogTagsMapper.deleteByBlogId(id);
        
        // 删除博客
        blogMapper.deleteById(id);
    }
    
    @Override
    public void incrementViews(Long id) {
        blogMapper.incrementViews(id);
    }
}
