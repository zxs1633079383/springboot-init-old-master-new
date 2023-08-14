package com.zlc.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zlc.model.entity.User;
import com.zlc.project.common.ErrorCode;
import com.zlc.project.exception.BusinessException;
import com.zlc.project.mapper.UserMapper;
import com.zlc.service.InnerUserService;
import com.zlc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    /***
     *  根据ak和sk获取用户信息
     * @param accessKey
     * @return
     */
    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("accessKey",accessKey);
//        queryWrapper.eq("secreKey",secreKey);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }
}
