package com.atguigu.tingshu.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.common.constant.RedisConstant;
import com.atguigu.tingshu.common.service.KafkaService;
import com.atguigu.tingshu.model.user.UserInfo;
import com.atguigu.tingshu.model.user.UserPaidAlbum;
import com.atguigu.tingshu.model.user.UserPaidTrack;
import com.atguigu.tingshu.user.mapper.UserInfoMapper;
import com.atguigu.tingshu.user.mapper.UserPaidAlbumMapper;
import com.atguigu.tingshu.user.mapper.UserPaidTrackMapper;
import com.atguigu.tingshu.user.service.UserInfoService;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private WxMaService wxMaService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private KafkaService kafkaService;

	@Autowired
	private UserPaidTrackMapper userPaidTrackMapper;

	@Autowired
	private UserPaidAlbumMapper userPaidAlbumMapper;

	/**
	 * 通过微信登录获取用户信息并返回token
	 *
	 * @param code 微信登录凭证
	 * @return 包含token的Map，如果登录失败则返回null
	 * @throws Exception 可能抛出的异常
	 */
	@SneakyThrows
    @Override
	public Map<String, String> wxLogin(String code) {
		// 获取微信用户唯一标识openId
		WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
		if (sessionInfo != null) {
			String openid = sessionInfo.getOpenid();
			UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getWxOpenId, openid));
			if (userInfo == null) {
				// 用户不存在，则创建新用户
				userInfo = new UserInfo();
				userInfo.setWxOpenId(openid);
				userInfo.setNickname("听友" + IdUtil.getSnowflakeNextIdStr());
				userInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
				userInfoMapper.insert(userInfo);
				// 创建资产账号
				kafkaService.send(KafkaConstant.QUEUE_USER_REGISTER, userInfo.getId().toString());
			}
			String token = IdUtil.simpleUUID();
			// 去除敏感信息
			UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class);
			redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token, userInfoVo, RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.MINUTES);
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("token", token);
			return hashMap;
		}
		return null;
	}

	@Override
	public Map<Long, Integer> userIsPaidTrackList(Long userId, Long albumId, List<Long> trackIdList) {
		// 是否买了专辑
		Long l = userPaidAlbumMapper.selectCount(new LambdaQueryWrapper<UserPaidAlbum>().eq(UserPaidAlbum::getUserId, userId).eq(UserPaidAlbum::getAlbumId, albumId));
		if (l > 0) {
			Map<Long, Integer> map = new HashMap<>();
			for (Long trackId : trackIdList) {
				map.put(trackId, 1);
			}
			return map;
		}
		// 是否买了单曲
		List<UserPaidTrack> userPaidTracks = userPaidTrackMapper.selectList(new LambdaQueryWrapper<UserPaidTrack>().eq(UserPaidTrack::getUserId, userId).eq(UserPaidTrack::getAlbumId, albumId).in(UserPaidTrack::getTrackId, trackIdList));
		if (CollectionUtil.isEmpty(userPaidTracks)) {
			Map<Long, Integer> map = new HashMap<>();
			for (Long trackId : trackIdList) {
				map.put(trackId, 0);
			}
			return map;
		}
		// 获取购买单曲
		List<Long> paidTrackIdList = userPaidTracks.stream().map(UserPaidTrack::getTrackId).collect(Collectors.toList());
		if (CollectionUtil.isNotEmpty(paidTrackIdList)) {
			Map<Long, Integer> map = new HashMap<>();
			for (Long trackId : trackIdList) {
				if (paidTrackIdList.contains(trackId)) {
					map.put(trackId, 1);
				}
			}
			return map;
		}
		return null;
	}
}
