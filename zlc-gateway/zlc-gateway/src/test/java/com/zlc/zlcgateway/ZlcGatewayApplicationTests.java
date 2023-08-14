package com.zlc.zlcgateway;

import com.zlc.service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZlcGatewayApplicationTests {

    @DubboReference
    private DemoService demoService;

    @Test
    void contextLoads() {
        String s = demoService.sayHello("123");
        System.out.println(s);
    }

}
