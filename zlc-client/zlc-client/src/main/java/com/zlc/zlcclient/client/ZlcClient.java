package com.zlc.zlcclient.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zlc.zlcclient.model.User;
import com.zlc.zlcclient.util.SignUntils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/***
 * 第三方接口的客户端
 */

public class ZlcClient {

    private String accessKey;

    private String secreKey;

    private static final String GATEWAY_HOST="http://localhost:8090";

    public ZlcClient(String accessKey, String secreKey) {
        this.accessKey = accessKey;
        this.secreKey = secreKey;
    }

    public String getName(String name){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("name",name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/",paramsMap);
        System.out.println(result);
        return  result;

    }

    public String getNameByPost( String name){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("name",name);
        String result = HttpUtil.post(GATEWAY_HOST+"/api/name/",paramsMap);
        System.out.println(result);
        return  result;

    }

    //参数配置
    private Map<String,String> getHeaderMap(String body){
        Map<String,String> map = new HashMap<>();
//        map.put("secreKey",secreKey);
        map.put("accessKey",accessKey);
        map.put("nonce", RandomUtil.randomNumbers(4));
        map.put("body",body);
        map.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", SignUntils.getSign(body,secreKey));

        return  map;
    }

    //生成签名


    public String getUserNameByPost( User user){
        String json = JSONUtil.toJsonStr(user);
        //配置参数
        Map<String,String> paramsMap = new HashMap<>();
//        String body = HttpRequest.post("http://localhost:7529/api/name/user")
        String body = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .charset(StandardCharsets.UTF_8)
                .addHeaders(getHeaderMap(json))
                .body(json).execute().body();
//        System.out.println();
        return  body;

    }

}
