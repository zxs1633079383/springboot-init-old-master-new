package com.zlc.zlcgateway;

import com.zlc.service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDubbo

public class ZlcGatewayApplication {

    @DubboReference
    private DemoService demoService;

    public static void main(String[] args) {
//        SpringApplication.run(ZlcGatewayApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(ZlcGatewayApplication.class,args);
        ZlcGatewayApplication application = context.getBean(ZlcGatewayApplication.class);
        application.sayHello("order");
    }

    public String sayHello(String order) {
        return demoService.sayHello(order);
    }

}
