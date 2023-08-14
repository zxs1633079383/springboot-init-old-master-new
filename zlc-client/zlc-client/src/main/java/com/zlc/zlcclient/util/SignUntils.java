package com.zlc.zlcclient.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/***
 * 签名工具类
 */
public class SignUntils {
    public static String getSign(String body, String secreKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body.toString() + "." + secreKey;
        return  md5.digestHex(content);
    }
}
