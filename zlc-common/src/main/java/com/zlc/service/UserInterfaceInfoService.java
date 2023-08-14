package com.zlc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.User;
import com.zlc.model.entity.UserInterfaceInfo;

/**
* @author 16330
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2023-08-12 15:11:09 即将进行dubbo调用
*/

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    public void validUserInterfaceInfo(UserInterfaceInfo interfaceInfo, boolean add);

    //1 . 数据口是否已分配密钥
    User getInvokeUser(String accessKey,String secreKey);

    //2. 数据查询该接口是否存在
    InterfaceInfo getInterInfo(String path,String method);


    boolean invokeCount (long interfaceInfoId, long userId);

}
