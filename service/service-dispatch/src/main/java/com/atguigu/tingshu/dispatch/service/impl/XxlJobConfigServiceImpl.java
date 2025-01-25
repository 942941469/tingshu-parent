package com.atguigu.tingshu.dispatch.service.impl;

import com.atguigu.tingshu.dispatch.mapper.XxlJobConfigMapper;
import com.atguigu.tingshu.dispatch.service.XxlJobConfigService;
import com.atguigu.tingshu.model.dispatch.XxlJobConfig;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"all"})
public class XxlJobConfigServiceImpl extends ServiceImpl<XxlJobConfigMapper, XxlJobConfig> implements XxlJobConfigService {

	@Autowired
	private XxlJobConfigMapper xxlJobConfigMapper;
}
