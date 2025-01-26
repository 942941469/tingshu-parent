package com.atguigu.tingshu.album.mapper;

import com.atguigu.tingshu.model.album.BaseAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface BaseAttributeMapper extends BaseMapper<BaseAttribute> {


    ArrayList<BaseAttribute> findAttribute(Long category1Id);
}
