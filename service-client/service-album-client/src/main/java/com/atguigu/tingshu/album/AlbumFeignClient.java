package com.atguigu.tingshu.album;

import com.atguigu.tingshu.album.impl.AlbumDegradeFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 专辑模块远程调用Feign接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-album", path = "api/album", fallback = AlbumDegradeFeignClient.class)
public interface AlbumFeignClient {
    @GetMapping("/albumInfo/getAlbumInfo/{id}")
    Result<AlbumInfo> getAlbumInfo(@PathVariable Long id);

    @GetMapping("/category/getCategoryView/{category3Id}")
    Result<BaseCategoryView> getCategoryViewBy3Id(@PathVariable Long category3Id);

    @GetMapping("/albumInfo/getAlbumStatVo/{albumId}")
    Result<AlbumStatVo> getAlbumStatVo(@PathVariable Long albumId);
}
