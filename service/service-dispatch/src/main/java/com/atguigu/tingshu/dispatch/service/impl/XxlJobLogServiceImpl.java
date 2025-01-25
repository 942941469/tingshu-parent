package com.atguigu.tingshu.dispatch.service.impl;

import com.atguigu.tingshu.dispatch.mapper.XxlJobLogMapper;
import com.atguigu.tingshu.dispatch.service.XxlJobLogService;
import com.atguigu.tingshu.model.dispatch.XxlJobLog;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"all"})
public class XxlJobLogServiceImpl extends ServiceImpl<XxlJobLogMapper, XxlJobLog> implements XxlJobLogService {

	@Autowired
	private XxlJobLogMapper xxlJobLogMapper;

}
