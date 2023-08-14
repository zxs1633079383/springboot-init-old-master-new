package com.zlc.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zlc.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService{

    //1 . 数据口是否已分配密钥
    User getInvokeUser(String accessKey);


}
