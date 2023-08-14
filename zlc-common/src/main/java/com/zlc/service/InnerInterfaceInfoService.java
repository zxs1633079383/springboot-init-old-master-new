package com.zlc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlc.model.entity.InterfaceInfo;

/**
* @author 16330
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-08-11 09:37:01
*/
public interface InnerInterfaceInfoService  {




    //2. 数据查询该接口是否存在
    InterfaceInfo getInterfaceInfo(String path,String method);


}
