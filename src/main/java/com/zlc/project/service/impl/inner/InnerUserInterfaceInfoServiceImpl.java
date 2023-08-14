package com.zlc.project.service.impl.inner;

import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.User;
import com.zlc.model.entity.UserInterfaceInfo;
import com.zlc.service.InnerUserInterfaceInfoService;
import com.zlc.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;



    // 计数
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }
}
