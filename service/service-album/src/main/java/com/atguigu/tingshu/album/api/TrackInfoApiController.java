package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.TrackInfoService;
import com.atguigu.tingshu.common.login.GuiGuLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.query.album.TrackInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumTrackListVo;
import com.atguigu.tingshu.vo.album.TrackInfoVo;
import com.atguigu.tingshu.vo.album.TrackListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

	@GetMapping("/trackInfo/getTrackInfo/{id}")
	@Operation(summary = "根据声音id查询声音信息")
	public Result<TrackInfo> getTrackInfo(@PathVariable("id") Integer id) {
		return Result.ok(trackInfoService.getById(id));
	}

	@PutMapping("/trackInfo/updateTrackInfo/{id}")
	@Operation(summary = "更新声音信息")
	public Result updateTrackInfo(@PathVariable("id") Long id, @RequestBody @Validated TrackInfoVo trackInfoVo) {
		trackInfoService.updateTrackInfoById(id, trackInfoVo);
		return Result.ok();
	}

	@DeleteMapping("/trackInfo/removeTrackInfo/{id}")
	@Operation(summary = "删除声音信息")
	public Result removeTrackInfo(@PathVariable("id") Integer id) {
		trackInfoService.removeTrackInfo(id);
		return Result.ok();
	}

	@GuiGuLogin(required = false)
	@Operation(summary = "查询当前用户查看专辑声音分页列表")
	@GetMapping("/trackInfo/findAlbumTrackPage/{albumId}/{page}/{limit}")
	public Result<Page<AlbumTrackListVo>> findAlbumTrackPage(@PathVariable("albumId") Long albumId, @PathVariable("page") int page, @PathVariable("limit") int limit) {
		Long userId = AuthContextHolder.getUserId();
		return Result.ok(trackInfoService.findUserTrackPageByAlbumId(new Page<AlbumTrackListVo>(page, limit), albumId, userId));
	}
}

