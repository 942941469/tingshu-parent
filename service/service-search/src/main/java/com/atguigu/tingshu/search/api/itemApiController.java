package com.atguigu.tingshu.search.api;

import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.search.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "专辑详情管理")
@RestController
@RequestMapping("api/search")
@SuppressWarnings({"all"})
public class itemApiController {

	@Autowired
	private ItemService itemService;

	@GetMapping("/albumInfo/{albumId}")
	@Operation(summary = "专辑详情")
	public Result<Map<String, Object>> albumInfo(@PathVariable Long albumId) {
		return Result.ok(itemService.albumInfo(albumId));
	}

}

