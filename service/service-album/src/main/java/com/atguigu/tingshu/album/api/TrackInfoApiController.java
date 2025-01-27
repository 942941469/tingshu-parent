package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.TrackInfoService;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.query.album.TrackInfoQuery;
import com.atguigu.tingshu.vo.album.TrackInfoVo;
import com.atguigu.tingshu.vo.album.TrackListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "声音管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class TrackInfoApiController {

	@Autowired
	private TrackInfoService trackInfoService;


	@PostMapping("/trackInfo/saveTrackInfo")
	@Operation(summary = "保存声音信息")
	public Result saveTrackInfo(@RequestBody TrackInfoVo trackInfoVo) {
		trackInfoService.saveTrackInfo(AuthContextHolder.getUserId() ,trackInfoVo);
		return Result.ok();
	}

	@PostMapping("/trackInfo/findUserTrackPage/{page}/{limit}")
	@Operation(summary = "分页查询声音信息")
	public Result<Page<TrackListVo>> findUserTrackPage(@PathVariable("page") int page, @PathVariable("limit") int limit, @RequestBody TrackInfoQuery trackInfoQuery) {
		trackInfoQuery.setUserId(AuthContextHolder.getUserId());
		Page<TrackListVo> trackListVoPage = new Page<>(page,limit);
		return Result.ok(trackInfoService.findUserTrackPage(trackListVoPage, trackInfoQuery));
	}
}

