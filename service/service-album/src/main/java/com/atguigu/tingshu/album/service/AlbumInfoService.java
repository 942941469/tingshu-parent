package com.atguigu.tingshu.album.service;

import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AlbumInfoService extends IService<AlbumInfo> {


    void saveAlbumInfo(Long userId, AlbumInfoVo albumInfoVo);

    void saveAlbumStat(Long albumId, String statType);

    Page<AlbumListVo> findUserAlbumPage(Page<AlbumListVo> pageInfo, AlbumInfoQuery albumInfoQuery);
}
