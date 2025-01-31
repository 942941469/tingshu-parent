package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.atguigu.tingshu.album.mapper.AlbumAttributeValueMapper;
import com.atguigu.tingshu.album.mapper.AlbumInfoMapper;
import com.atguigu.tingshu.album.mapper.AlbumStatMapper;
import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.login.GuiGuLogin;
import com.atguigu.tingshu.common.service.KafkaService;
import com.atguigu.tingshu.model.album.AlbumAttributeValue;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.AlbumStat;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.atguigu.tingshu.common.result.ResultCodeEnum.NOT_DELETE;

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

	@Autowired
	private KafkaService kafkaService;

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
		if (albumInfo.getIsOpen().equals("1")) {
			kafkaService.send(KafkaConstant.QUEUE_ALBUM_UPPER, albumId.toString());
		}
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

	/**
	 * 查询用户专辑分页列表
	 *
	 * @param pageInfo 分页信息对象，包含页码、每页显示数量等信息
	 * @param albumInfoQuery 专辑查询条件对象，用于筛选符合条件的专辑
	 * @return 分页后的专辑列表对象，包含专辑的详细信息和分页信息
	 */
	@Override
	public Page<AlbumListVo> findUserAlbumPage(Page<AlbumListVo> pageInfo, AlbumInfoQuery albumInfoQuery) {
		return albumInfoMapper.findUserAlbumPage(pageInfo, albumInfoQuery);
	}

	/**
	 * 删除专辑信息
	 *
	 * @param id 专辑的ID
	 * @throws GuiguException 如果专辑包含曲目数量大于0，则抛出GuiguException异常，表示不能删除该专辑
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeAlbumInfo(Integer id) {
		AlbumInfo albumInfo = albumInfoMapper.selectById(id);
		if (albumInfo.getIncludeTrackCount() > 0) {
			throw new GuiguException(NOT_DELETE);
		}
		albumInfoMapper.deleteById(id);
		albumAttributeValueMapper.delete(new LambdaQueryWrapper<AlbumAttributeValue>().eq(AlbumAttributeValue::getAlbumId, id));
		albumStatMapper.delete(new LambdaQueryWrapper<AlbumStat>().eq(AlbumStat::getAlbumId, id));
		kafkaService.send(KafkaConstant.QUEUE_ALBUM_LOWER, id.toString());
	}

	/**
	 * 根据专辑ID获取专辑信息
	 *
	 * @param id 专辑的ID
	 * @return 包含专辑属性值的专辑信息对象
	 */
	@Override
	public AlbumInfo getAlbumInfo(Long id) {
		AlbumInfo albumInfo = albumInfoMapper.selectById(id);
		List<AlbumAttributeValue> albumAttributeValues = albumAttributeValueMapper.selectList(new LambdaQueryWrapper<AlbumAttributeValue>().eq(AlbumAttributeValue::getAlbumId, id));
		albumInfo.setAlbumAttributeValueVoList(albumAttributeValues);
		return albumInfo;
	}

	/**
	 * 更新专辑信息
	 *
	 * @param id 专辑ID
	 * @param albumInfoVo 包含要更新的专辑信息的VO对象
	 * @throws Exception 如果在事务执行过程中发生任何异常，则事务将回滚
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAlbumInfo(Long id, AlbumInfoVo albumInfoVo) {
		AlbumInfo albumInfo = BeanUtil.copyProperties(albumInfoVo, AlbumInfo.class);
		albumInfo.setId(id);
		albumInfoMapper.updateById(albumInfo);
		albumAttributeValueMapper.delete(new LambdaQueryWrapper<AlbumAttributeValue>().eq(AlbumAttributeValue::getAlbumId, id));
		List<AlbumAttributeValue> albumAttributeValueVoList = albumInfo.getAlbumAttributeValueVoList();
		if (CollectionUtil.isNotEmpty(albumAttributeValueVoList)) {
			albumAttributeValueVoList.forEach(albumAttributeValueVo -> {
				AlbumAttributeValue albumAttributeValue = BeanUtil.copyProperties(albumAttributeValueVo, AlbumAttributeValue.class);
				albumAttributeValue.setAlbumId(id);
				albumAttributeValueMapper.insert(albumAttributeValue);
			});
		}
		if (albumInfo.getIsOpen().equals("1")) {
			kafkaService.send(KafkaConstant.QUEUE_ALBUM_UPPER, id.toString());
		} else {
			kafkaService.send(KafkaConstant.QUEUE_ALBUM_LOWER, id.toString());
		}
	}

	/**
	 * 查询用户所有专辑列表
	 *
	 * @param userId 用户ID
	 * @return 返回用户最近添加的10个专辑信息列表
	 */
	@Override
	public List<AlbumInfo> findUserAllAlbumList(Long userId) {
		return albumInfoMapper.selectList(new LambdaQueryWrapper<AlbumInfo>().eq(AlbumInfo::getUserId, userId).orderByDesc(AlbumInfo::getId).last("limit 10"));
	}
}
