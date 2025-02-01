package com.atguigu.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.result.Result;

import com.atguigu.tingshu.model.album.BaseAttribute;
import com.atguigu.tingshu.model.album.BaseCategory3;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "分类管理")
@RestController
@RequestMapping(value="/api/album")
@SuppressWarnings({"all"})
public class BaseCategoryApiController {

	@Autowired
	private BaseCategoryService baseCategoryService;


	@GetMapping("/category/getBaseCategoryList")
	@Operation(summary = "获取基础分类列表")
	public Result<List<JSONObject>> getBaseCategoryList(){
		return Result.ok(baseCategoryService.getBaseCategoryList());
	}

	@GetMapping("/category/findAttribute/{category1Id}")
	@Operation(summary = "根据一级分类Id获取分类属性（标签）列表")
	public Result<List<BaseAttribute>> findAttribute(@PathVariable("category1Id") Long category1Id) {
		return Result.ok(baseCategoryService.findAttribute(category1Id));
	}

	@GetMapping("/category/getCategoryView/{category3Id}")
	@Operation(summary = "根据三级分类Id 获取到分类信息")
	public Result<BaseCategoryView> getCategoryView(@PathVariable("category3Id") Long category3Id) {
		return Result.ok(baseCategoryService.getCategoryView(category3Id));
	}

	@Operation(summary = "根据一级分类ID查询该一级分类下包含所有三级分类列表")
	@GetMapping("/category/findTopBaseCategory3/{category1Id}")
	public Result<List<BaseCategory3>> getTop7BaseCategory3(@PathVariable Long category1Id) {
		return Result.ok(baseCategoryService.getTop7BaseCategory3(category1Id));
	}

	@Operation(summary = "根据一级分类ID查询当前分类包含所有二级分类以及二级分类下三级分类列表")
	@GetMapping("/category/getBaseCategoryList/{category1Id}")
	public Result<JSONObject> getCategoryListByCategory1Id(@PathVariable Long category1Id) {
		return Result.ok(baseCategoryService.getCategoryListByCategory1Id(category1Id));
	}
}

