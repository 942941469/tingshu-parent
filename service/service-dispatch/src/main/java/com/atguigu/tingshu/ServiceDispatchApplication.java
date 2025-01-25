package com.atguigu.tingshu;

import com.atguigu.tingshu.dispatch.service.XxlJobConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceDispatchApplication {

    @Autowired
    private XxlJobConfigService xxlJobConfigService;

    public static void main(String[] args) {
        SpringApplication.run(ServiceDispatchApplication.class, args);
    }

}
