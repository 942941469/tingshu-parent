package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.*;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.model.album.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings({"all"})
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

	@Autowired
	private BaseCategory1Mapper baseCategory1Mapper;

	@Autowired
	private BaseCategory2Mapper baseCategory2Mapper;

	@Autowired
	private BaseCategory3Mapper baseCategory3Mapper;

	@Autowired
	private BaseCategoryViewMapper baseCategoryViewMapper;

	@Autowired
	private BaseAttributeMapper baseAttributeMapper;


	/**
	 * 获取基础分类列表
	 *
	 * 从数据库中获取基础分类信息，并按照一级分类、二级分类、三级分类的层级结构进行封装，最终返回一个包含所有分类信息的列表。
	 *
	 * @return 包含所有分类信息的列表
	 */
	@Override
	public List<JSONObject> getBaseCategoryList() {
		List<JSONObject> result = new ArrayList<>();
		List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
		if (CollectionUtil.isNotEmpty(baseCategoryViews)) {
			Map<Long, List<BaseCategoryView>> baseCategoryView1 = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
			// 封装一级分类
			for (Map.Entry<Long, List<BaseCategoryView>> entry1 : baseCategoryView1.entrySet()) {
				JSONObject jsonObject1 = new JSONObject();
				Long categoryId1 = entry1.getKey();
				jsonObject1.put("categoryId", categoryId1);
				String category1Name = entry1.getValue().get(0).getCategory1Name();
				jsonObject1.put("categoryName", category1Name);
				// 封装二级分类
				Map<Long, List<BaseCategoryView>> baseCategoryView2 = entry1.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
				List<JSONObject> baseCategoryList2 = new ArrayList<>();
				for (Map.Entry<Long, List<BaseCategoryView>> entry2 : baseCategoryView2.entrySet()) {
					JSONObject jsonObject2 = new JSONObject();
					Long categoryId2 = entry2.getKey();
					jsonObject2.put("categoryId", categoryId2);
					String category2Name = entry2.getValue().get(0).getCategory2Name();
					jsonObject2.put("categoryName", category2Name);
					// 封装三级分类
					List<JSONObject> baseCategoryList3 = new ArrayList<>();
					// 这里使用entry2遍历,而不是entry1的原因是,entry2是二级分类,它和对应的二级分类下的三级分类是一一对应的,这样可以避免重复封装
					for (BaseCategoryView entry: entry2.getValue()) {
						JSONObject jsonObject3 = new JSONObject();
						jsonObject3.put("categoryId", entry.getCategory3Id());
						jsonObject3.put("categoryName", entry.getCategory3Name());
						baseCategoryList3.add(jsonObject3);
					}
					baseCategoryList2.add(jsonObject2);
					jsonObject2.put("categoryChild", baseCategoryList3);
				}
				jsonObject1.put("categoryChild", baseCategoryList2);
				result.add(jsonObject1);
			}
		}
		return result;
	}

	@Override
	public List<BaseAttribute> findAttribute(Long category1Id) {
		return baseAttributeMapper.findAttribute(category1Id);
	}

	/**
	 * 获取分类视图信息
	 *
	 * @param category3Id 三级分类ID
	 * @return BaseCategoryView 分类视图对象
	 */
	@Override
	public BaseCategoryView getCategoryView(Long category3Id) {
		return baseCategoryViewMapper.selectById(category3Id);
	}

	/**
	 * 获取一级分类下前7个三级分类
	 *
	 * @param category1Id 一级分类ID
	 * @return 返回前7个三级分类的列表
	 */
	@Override
	public List<BaseCategory3> getTop7BaseCategory3(Long category1Id) {
		List<BaseCategory2> baseCategory2 = baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>().eq(BaseCategory2::getCategory1Id, category1Id));
		if (CollectionUtil.isNotEmpty(baseCategory2)) {
			List<Long> collect = baseCategory2.stream().map(BaseCategory2::getId).collect(Collectors.toList());
			return baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>().in(BaseCategory3::getCategory2Id, collect).orderByAsc(BaseCategory3::getId).last("limit 7"));
		}
		return null;
	}

	@Override
	public JSONObject getCategoryListByCategory1Id(Long category1Id) {
		List<JSONObject> baseCategoryList = getBaseCategoryList();
		for (JSONObject jsonObject: baseCategoryList) {
			Long categoryId = jsonObject.getLong("categoryId");
			if (categoryId.equals(category1Id)) {
				return jsonObject;
            }
		}
		return null;
	}
}
