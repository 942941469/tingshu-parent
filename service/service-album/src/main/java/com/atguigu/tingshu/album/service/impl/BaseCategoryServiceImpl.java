package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.BaseCategory1Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategory2Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategory3Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategoryViewMapper;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.model.album.BaseCategory1;
import com.atguigu.tingshu.model.album.BaseCategoryView;
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
}
