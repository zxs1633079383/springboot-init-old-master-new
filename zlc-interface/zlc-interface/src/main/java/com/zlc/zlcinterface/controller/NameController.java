package com.zlc.zlcinterface.controller;


import com.zlc.zlcclient.model.User;
import com.zlc.zlcclient.util.SignUntils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getName(String name,HttpServletRequest request){

        System.out.println("request:" + request.getHeader("yupi"));
        return "Get:你的名字是: " + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam  String name){
        return "Post:你的名字是: " + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String body = request.getHeader("body");
        String sign = request.getHeader("sign");
        // todo 去数据口查询是佛u分配给用户.


        if(! accessKey.equals("yupi") ){
            throw  new RuntimeException("无权限");
        }
        if(Long.parseLong(nonce) > 10000){
            throw  new RuntimeException("无权限");
        }
        // todo 时间和当前时间不大于5分钟

        // 实际是数据口查出
        String serverSign = SignUntils.getSign(body, "abcdefg");

        if(!serverSign.equals(sign)){
            throw  new RuntimeException("无权限");
        }

        String result = user.getName();
        //调用成功后,+1



        return "Post:你的用户名是: " + result;
    }



}
