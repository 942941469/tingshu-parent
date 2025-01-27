package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.tingshu.album.mapper.AlbumInfoMapper;
import com.atguigu.tingshu.album.mapper.TrackInfoMapper;
import com.atguigu.tingshu.album.mapper.TrackStatMapper;
import com.atguigu.tingshu.album.service.TrackInfoService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.model.album.TrackStat;
import com.atguigu.tingshu.vo.album.TrackInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class TrackInfoServiceImpl extends ServiceImpl<TrackInfoMapper, TrackInfo> implements TrackInfoService {

    @Autowired
    private TrackInfoMapper trackInfoMapper;

	@Autowired
	private TrackStatMapper trackStatMapper;

	@Autowired
	private AlbumInfoMapper albumInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrackInfo(Long userId, TrackInfoVo trackInfoVo) {
        TrackInfo trackInfo = BeanUtil.copyProperties(trackInfoVo, TrackInfo.class);
		trackInfo.setUserId(userId);
		trackInfo.setOrderNum(1);
		trackInfo.setStatus(SystemConstant.TRACK_STATUS_PASS);
		trackInfo.setMediaType("mp3");
		trackInfo.setMediaDuration(BigDecimal.valueOf(1));
		trackInfo.setMediaSize(1L);
		trackInfo.setMediaUrl("http://www.baidu.com");
		trackInfo.setMediaFileId("1");
		TrackInfo track = trackInfoMapper.selectOne(new LambdaQueryWrapper<TrackInfo>()
				.eq(TrackInfo::getAlbumId, trackInfo.getAlbumId())
				.orderByDesc(TrackInfo::getOrderNum)
				.last("LIMIT 1"));
		if (track != null) {
			trackInfo.setId(track.getId());
		}
		trackInfoMapper.insert(trackInfo);
		AlbumInfo albumInfo = albumInfoMapper.selectById(trackInfo.getAlbumId());
		albumInfo.setIncludeTrackCount(albumInfo.getIncludeTrackCount() + 1);
		albumInfoMapper.updateById(albumInfo);
		this.saveTrackStat(trackInfo.getId(), SystemConstant.TRACK_STAT_PLAY);
		this.saveTrackStat(trackInfo.getId(), SystemConstant.TRACK_STAT_COLLECT);
		this.saveTrackStat(trackInfo.getId(), SystemConstant.TRACK_STAT_COMMENT);
		this.saveTrackStat(trackInfo.getId(), SystemConstant.TRACK_STAT_PRAISE);
    }

	public void saveTrackStat(Long trackId, String statType) {
		trackStatMapper.insert(new TrackStat(trackId, statType, 0));
	}
}
