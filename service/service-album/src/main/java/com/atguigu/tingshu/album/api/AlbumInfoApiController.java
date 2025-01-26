package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class AlbumInfoApiController {

	@Autowired
	private AlbumInfoService albumInfoService;

	@PostMapping("/albumInfo/saveAlbumInfo")
	@Operation(summary = "保存专辑信息")
	public Result saveAlbumInfo(@RequestBody @Validated AlbumInfoVo albumInfoVo) {
		albumInfoService.saveAlbumInfo(AuthContextHolder.getUserId(), albumInfoVo);
		return Result.ok();
	}

	@PostMapping("/albumInfo/findUserAlbumPage/{page}/{limit}")
	@Operation(summary = "查询用户专辑分页")
	public Result<Page<AlbumListVo>> findUserAlbumPage(@PathVariable Integer page, @PathVariable Integer limit, @RequestBody AlbumInfoQuery albumInfoQuery) {
		Page<AlbumListVo> pageInfo = new Page<>(page, limit);
		return Result.ok(albumInfoService.findUserAlbumPage(pageInfo, albumInfoQuery));
	}

	@DeleteMapping("/albumInfo/removeAlbumInfo/{id}")
	@Operation(summary = "删除专辑信息")
	public Result removeAlbumInfo(@PathVariable Integer id) {
		albumInfoService.removeAlbumInfo(id);
		return Result.ok();
	}

	@GetMapping("/albumInfo/getAlbumInfo/{id}")
	@Operation(summary = "根据ID查询专辑信息")
	public Result<AlbumInfo> getAlbumInfo(@PathVariable Integer id) {
		return Result.ok(albumInfoService.getAlbumInfo(id));
	}

	@PutMapping("/albumInfo/updateAlbumInfo/{id}")
	@Operation(summary = "更新专辑信息")
	public Result updateAlbumInfo(@PathVariable Long id, @RequestBody @Validated AlbumInfoVo albumInfoVo) {
		albumInfoService.updateAlbumInfo(id, albumInfoVo);
		return Result.ok();
	}

	@GetMapping("/albumInfo/findUserAllAlbumList")
	@Operation(summary = "查询用户所有专辑列表")
	public Result<List<AlbumInfo>> findUserAllAlbumList() {
		return Result.ok(albumInfoService.findUserAllAlbumList(AuthContextHolder.getUserId()));
	}
}

