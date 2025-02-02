package com.atguigu.tingshu.user.service.impl;

import com.atguigu.tingshu.common.util.MongoUtil;
import com.atguigu.tingshu.model.user.UserListenProcess;
import com.atguigu.tingshu.user.service.UserListenProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@SuppressWarnings({"all"})
public class UserListenProcessServiceImpl implements UserListenProcessService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public BigDecimal getTrackBreakSecond(Long userId, Long trackId) {
		String collectionName = MongoUtil.getCollectionName(MongoUtil.MongoCollectionEnum.USER_LISTEN_PROCESS, userId);
		UserListenProcess userListenProcess = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("userId").is(userId).and("trackId").is(trackId)), UserListenProcess.class, collectionName);
		if (userListenProcess != null) {
			return userListenProcess.getBreakSecond();
		}
		return new BigDecimal(0);
	}
}
