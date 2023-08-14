package com.zlc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlc.model.entity.InterfaceInfo;

/**
* @author 16330
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-08-11 09:37:01
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {


    public void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);


}
