package com.zlc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlc.project.common.ErrorCode;
import com.zlc.project.exception.BusinessException;
import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.User;
import com.zlc.model.entity.UserInterfaceInfo;
import com.zlc.project.mapper.UserInterfaceInfoMapper;
import com.zlc.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
* @author 16330
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
* @createDate 2023-08-12 15:11:09
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo interfaceInfo, boolean add) {
        if(interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(add){
            if(interfaceInfo.getInterfaceInfoId() <=0 || interfaceInfo.getUserId()<=0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if(interfaceInfo.getLeft_num()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余接口数量不能小于0");
        }
    }

    @Override
    public User getInvokeUser(String accessKey, String secreKey) {
        return null;
    }

    @Override
    public InterfaceInfo getInterInfo(String path, String method) {
        return null;
    }

    public boolean invokeCount(long interfaceInfoId, long userId){
        //校验
        if(interfaceInfoId <= 0 || userId<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        updateWrapper.gt("left_Num",0);
        updateWrapper.setSql("left_Num = left_Num -1,total_Num = total_Num +1");
        boolean result = this.update(updateWrapper);
        return result;

    };
}




