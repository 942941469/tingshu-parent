package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.atguigu.tingshu.album.mapper.AlbumAttributeValueMapper;
import com.atguigu.tingshu.album.mapper.AlbumInfoMapper;
import com.atguigu.tingshu.album.mapper.AlbumStatMapper;
import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.model.album.AlbumAttributeValue;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.AlbumStat;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

	@Autowired
	private AlbumInfoMapper albumInfoMapper;

	@Autowired
	private AlbumAttributeValueMapper albumAttributeValueMapper;

	@Autowired
	private AlbumStatMapper albumStatMapper;

	/**
	 * 保存专辑信息
	 *
	 * @param userId 用户ID
	 * @param albumInfoVo 专辑信息VO对象
	 * @throws Exception 如果保存过程中发生错误，则抛出异常
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAlbumInfo(Long userId, AlbumInfoVo albumInfoVo) {
		// 保存专辑基本信息
		AlbumInfo albumInfo = BeanUtil.copyProperties(albumInfoVo, AlbumInfo.class);
		albumInfo.setUserId(userId);
		if (!SystemConstant.ALBUM_PAY_TYPE_FREE.equals(albumInfo.getPayType())) {
			albumInfo.setTracksForFree(5);
		}
		albumInfo.setStatus(SystemConstant.ALBUM_STATUS_PASS);
		albumInfoMapper.insert(albumInfo);
		List<AlbumAttributeValue> albumAttributeValueVoList = albumInfo.getAlbumAttributeValueVoList();
		Long albumId = albumInfo.getId();
		// 作品标签
		if (CollectionUtil.isNotEmpty(albumAttributeValueVoList)) {
			albumAttributeValueVoList.forEach(albumAttributeValueVo -> {
				AlbumAttributeValue albumAttributeValue = BeanUtil.copyProperties(albumAttributeValueVo, AlbumAttributeValue.class);
				albumAttributeValue.setAlbumId(albumId);
				albumAttributeValueMapper.insert(albumAttributeValue);
			});
		}
		// 作品统计量初始化
		this.saveAlbumStat(albumId, SystemConstant.ALBUM_STAT_PLAY);
		this.saveAlbumStat(albumId, SystemConstant.ALBUM_STAT_SUBSCRIBE);
		this.saveAlbumStat(albumId, SystemConstant.ALBUM_STAT_BUY);
		this.saveAlbumStat(albumId, SystemConstant.ALBUM_STAT_COMMENT);
	}

	/**
	 * 保存专辑统计信息
	 *
	 * @param albumId 专辑ID
	 * @param statType 统计类型，例如播放次数、订阅数等
	 */
	@Override
	public void saveAlbumStat(Long albumId, String statType) {
		albumStatMapper.insert(new AlbumStat(albumId, statType, 0));
	}
}
