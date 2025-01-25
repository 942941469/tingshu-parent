package com.atguigu.tingshu.dispatch.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xxl-job-admin模块JobInfoController类添加rest接口
 * @RequestMapping("/addJob")
 *        @ResponseBody
 *    public ReturnT<String> addJob(@RequestBody XxlJobInfo jobInfo) {
 * 		return xxlJobService.add(jobInfo);
 *    }
 *
 *    @RequestMapping("/updateJob")
 *    @ResponseBody
 *    public ReturnT<String> updateJob(@RequestBody XxlJobInfo jobInfo) {
 * 		return xxlJobService.update(jobInfo);
 *    }
 *
 *    @RequestMapping("/removeJob")
 *    @ResponseBody
 *    public ReturnT<String> removeJob(int id) {
 * 		return xxlJobService.remove(id);
 *    }
 *
 *    @RequestMapping("/stopJob")
 *    @ResponseBody
 *    public ReturnT<String> pauseJob(int id) {
 * 		return xxlJobService.stop(id);
 *    }
 *
 *    @RequestMapping("/startJob")
 *    @ResponseBody
 *    public ReturnT<String> startJob(int id) {
 * 		return xxlJobService.start(id);
 *    }
 *
 *    @RequestMapping("/triggerJob")
 *    @ResponseBody
 * 	//@PermissionLimit(limit = false)
 * 	public ReturnT<String> triggerJob1(int id, String executorParam, String addressList) {
 * 		// force cover job param
 * 		if (executorParam == null) {
 * 			executorParam = "";
 *        }
 *
 * 		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
 * 		return ReturnT.SUCCESS;
 *    }
 */
@Slf4j
@Component
public class XxlJobHelper {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses = "http://139.198.127.41:8080/xxl-job-admin";

    private RestTemplate restTemplate = new RestTemplate();

    private static final String ADD_URL = "/jobinfo/addJob";
    private static final String UPDATE_URL = "/jobinfo/updateJob";
    private static final String REMOVE_URL = "/jobinfo/removeJob";
    private static final String PAUSE_URL = "/jobinfo/pauseJob";
    private static final String START_URL = "/jobinfo/startJob";
    private static final String TRIGGER_URL = "/jobinfo/triggerJob";


    public Long add(String executorHandler, String executorParam, String corn, String desc){
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(1);
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setAuthor("qy");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(corn);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(executorParam);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        String json2 = JSON.toJSONString(xxlJobInfo);
        String xxlJobId = doPost(adminAddresses + ADD_URL, json2);
        return Long.parseLong(xxlJobId);
    }

    public String update(Long id, String executorHandler, String executorParam, String corn, String desc){
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(id.intValue());
        xxlJobInfo.setJobGroup(1);
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setAuthor("qy");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(corn);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(executorParam);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        String json = JSON.toJSONString(xxlJobInfo);
        return doPost(adminAddresses + UPDATE_URL, json);
    }

    public String trigger(Long id, String executorParam){
        Map<String,Object> param = new HashMap<>();
        param.put("id", id);
        param.put("executorParam", executorParam);
        String json = JSON.toJSONString(param);
        return doPost(adminAddresses + TRIGGER_URL, json);
    }

    public String remove(Long id){
        Map<String,Object> param = new HashMap<>();
        param.put("id", id);
        String json = JSON.toJSONString(param);
        return doPost(adminAddresses + REMOVE_URL, json);
    }

    public String pause(Long id){
        Map<String,Object> param = new HashMap<>();
        param.put("id", id);
        String json = JSON.toJSONString(param);
        return doPost(adminAddresses + PAUSE_URL, json);
    }

    public String start(Long id){
        Map<String,Object> param = new HashMap<>();
        param.put("id", id);
        String json = JSON.toJSONString(param);
        return doPost(adminAddresses + START_URL, json);
    }

    public String doPost(String url, String json){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 设置请求头的方式，根据需要设置
        List<String> cookies = new ArrayList<>();
        cookies.add("XXL_JOB_LOGIN_IDENTITY=7b226964223a312c22757365726e616d65223a2261646d696e222c2270617373776f7264223a226531306164633339343962613539616262653536653035376632306638383365222c22726f6c65223a312c227065726d697373696f6e223a6e756c6c7d");
        headers.put(HttpHeaders.COOKIE, cookies);

        HttpEntity<String> entity = new HttpEntity<>(json ,headers);
        log.info(entity.toString());
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, entity, String.class);
        if(stringResponseEntity.getStatusCodeValue() == 200) {
            JSONObject object = JSON.parseObject(stringResponseEntity.getBody().toString());
            if(object.getIntValue("code") == 200) {
                return object.getString("content");
            } else {
                throw new GuiguException(ResultCodeEnum.XXL_JOB_ERROR);
            }
        }
        throw new GuiguException(ResultCodeEnum.XXL_JOB_ERROR);
    }

}