package com.zlc.zlcinterface;

import com.zlc.zlcclient.client.ZlcClient;
import com.zlc.zlcclient.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ZlcInterfaceApplicationTests {

    @Resource
    private ZlcClient zlcClient;

    @Test
    void contextLoads() {
        String result = zlcClient.getName("yupi");
        User user = new User();
        user.setName("zlc");
        String userNameByPost = zlcClient.getUserNameByPost(user);
        System.out.println("test==>" + result);
        System.out.println(userNameByPost);
    }

}
