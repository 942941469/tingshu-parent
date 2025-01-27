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
import com.atguigu.tingshu.query.album.TrackInfoQuery;
import com.atguigu.tingshu.vo.album.TrackInfoVo;
import com.atguigu.tingshu.vo.album.TrackListVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

	/**
	 * 保存音轨信息
	 *
	 * @param userId 用户ID
	 * @param trackInfoVo 音轨信息对象（包含音轨的详细信息）
	 *
	 * 该方法将音轨信息保存到数据库中，并更新相关专辑的包含音轨数量。
	 * 首先，将传入的音轨信息对象（TrackInfoVo）转换为TrackInfo实体类对象，并设置必要的字段值，如用户ID、订单号、状态、媒体类型、媒体时长、媒体大小、媒体URL和媒体文件ID。
	 * 然后，通过查询数据库，检查是否存在与当前音轨相同专辑ID且订单号最大的音轨记录。如果存在，则更新当前音轨的ID为查询到的音轨ID；如果不存在，则插入新的音轨记录。
	 * 接着，更新相关专辑的包含音轨数量，将其加1。
	 * 最后，保存与当前音轨相关的统计信息，包括播放、收藏、评论和点赞。
	 */
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

	/**
	 * 分页查询用户音轨列表
	 *
	 * @param trackListVoPage 分页信息对象，包含当前页码、每页显示数量等信息
	 * @param trackInfoQuery  音轨查询条件对象，包含用户ID、音轨名称等查询条件
	 * @return 分页后的用户音轨列表对象，包含用户音轨信息和分页信息
	 *
	 * 该方法用于根据用户ID和音轨查询条件，分页查询用户的音轨列表。
	 * 方法接收两个参数：一个是分页信息对象，包含当前页码、每页显示数量等信息；另一个是音轨查询条件对象，包含用户ID、音轨名称等查询条件。
	 * 方法内部调用 trackInfoMapper 的 findUserTrackPage 方法，将分页信息对象和音轨查询条件对象传递给它，并返回分页后的用户音轨列表对象。
	 */
	@Override
	public Page<TrackListVo> findUserTrackPage(Page<TrackListVo> trackListVoPage, TrackInfoQuery trackInfoQuery) {
		return trackInfoMapper.findUserTrackPage(trackListVoPage, trackInfoQuery);
	}
	@Override
	public void saveTrackStat(Long trackId, String statType) {
		trackStatMapper.insert(new TrackStat(trackId, statType, 0));
	}
}
