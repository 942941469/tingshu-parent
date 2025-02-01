package com.atguigu.tingshu.search.service.impl;

import cn.hutool.core.lang.Assert;
import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.atguigu.tingshu.search.service.ItemService;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class ItemServiceImpl implements ItemService {

    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public Map<String, Object> albumInfo(Long albumId) {
        Map<String, Object> mapResult = new ConcurrentHashMap<>();
        CompletableFuture<AlbumInfo> albumInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            AlbumInfo albumInfo = albumFeignClient.getAlbumInfo(albumId).getData();
            Assert.notNull(albumInfo, "专辑信息为空");
            mapResult.put("albumInfo", albumInfo);
            return albumInfo;
        }, threadPoolExecutor);
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> {
            AlbumStatVo albumStatVo = albumFeignClient.getAlbumStatVo(albumId).getData();
            Assert.notNull(albumStatVo, "专辑统计信息为空");
            mapResult.put("albumStatVo", albumStatVo);
        }, threadPoolExecutor);
        CompletableFuture<Void> voidCompletableFuture2 = albumInfoCompletableFuture.thenAcceptAsync(albumInfo -> {
            BaseCategoryView baseCategoryView = albumFeignClient.getCategoryViewBy3Id(albumInfo.getCategory3Id()).getData();
            Assert.notNull(baseCategoryView, "分类信息为空");
            mapResult.put("baseCategoryView", baseCategoryView);
        }, threadPoolExecutor);
        CompletableFuture<Void> voidCompletableFuture3 = albumInfoCompletableFuture.thenAcceptAsync(albumInfo -> {
            UserInfoVo userInfoVo = userFeignClient.getUserInfoVoByUserId(albumInfo.getUserId()).getData();
            Assert.notNull(userInfoVo, "主播信息为空");
            mapResult.put("announcer", userInfoVo);
        }, threadPoolExecutor);
        CompletableFuture.allOf(albumInfoCompletableFuture, voidCompletableFuture1, voidCompletableFuture2, voidCompletableFuture3).join();
        return mapResult;
    }
}
