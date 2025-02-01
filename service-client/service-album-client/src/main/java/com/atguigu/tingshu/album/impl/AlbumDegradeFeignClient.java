package com.atguigu.tingshu.album.impl;


import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlbumDegradeFeignClient implements AlbumFeignClient {


    @Override
    public Result<AlbumInfo> getAlbumInfo(Long id) {
        log.warn("获取专辑信息失败！");
        return Result.fail();
    }

    @Override
    public Result<BaseCategoryView> getCategoryViewBy3Id(Long category3Id) {
        log.warn("获取专辑分类失败！");
        return Result.fail();
    }

    @Override
    public Result<AlbumStatVo> getAlbumStatVo(Long albumId) {
        log.warn("获取专辑统计数据失败！");
        return Result.fail();
    }
}
