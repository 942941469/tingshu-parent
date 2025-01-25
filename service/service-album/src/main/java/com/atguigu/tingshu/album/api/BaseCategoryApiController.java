package com.atguigu.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

	/**
	 * 获取基础分类列表
	 * @return 含基础分类列表的 Result 对象，如果成功，则 Result 对象中包含基础分类列表；如果失败，则 Result 对象中包含错误信息
	 */

	@GetMapping("/category/getBaseCategoryList")
	@Operation(summary = "获取基础分类列表")
	public Result<List<JSONObject>> getBaseCategoryList(){
		return Result.ok(baseCategoryService.getBaseCategoryList());
	}
}

